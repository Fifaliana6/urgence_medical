import { useEffect, useState, useCallback, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getVisiteDetail, sortirPatient, hospitaliserPatient, finHospitalisation } from '../api/visitApi';
import { ouvrirConsultation, ajouterPrescription, demanderExamen } from '../api/consultationApi';
import { getFactureParVisite, telechargerFacturePdf, marquerFacturePayee } from '../api/invoiceApi';
import { telechargerRapportExamen } from '../api/examApi';
import { listerLits } from '../api/resourceApi';
import { declencherTelechargement } from '../utils/download';
import { obtenirMessageErreur } from '../utils/apiError';
import { useAuth } from '../context/AuthContext';
import { useNotifications } from '../context/NotificationContext';
import UrgencyBadge from '../components/UrgencyBadge';
import StatusBadge from '../components/StatusBadge';

const TYPES_EXAMEN = ['BIOLOGIE', 'IMAGERIE'];
const SERVICES_MEDICAUX = ['CARDIOLOGIE', 'REANIMATION', 'MEDECINE_GENERALE', 'PEDIATRIE', 'CHIRURGIE'];

export default function VisitDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const { notifications } = useNotifications() || { notifications: [] };
  const dernierNombreNotifs = useRef(0);

  const [visite, setVisite] = useState(null);
  const [facture, setFacture] = useState(null);
  const [litsDisponibles, setLitsDisponibles] = useState([]);
  const [chargement, setChargement] = useState(true);
  const [erreur, setErreur] = useState('');
  const [action, setAction] = useState(false);
  const [telechargementEnCours, setTelechargementEnCours] = useState(null);

  const [diagnostic, setDiagnostic] = useState('');
  const [planTraitement, setPlanTraitement] = useState('');

  const [medicaments, setMedicaments] = useState([{ medicament: '', dosage: '', duree: '', instructions: '' }]);

  const [typeExamen, setTypeExamen] = useState('BIOLOGIE');
  const [libelleExamen, setLibelleExamen] = useState('');

  const [serviceHospitalisation, setServiceHospitalisation] = useState('');
  const [litChoisiId, setLitChoisiId] = useState('');

  const peutAgirMedecin = user?.role === 'MEDECIN';
  const peutVoirFacture = ['ADMIN', 'RECEPTIONIST', 'MEDECIN'].includes(user?.role);
  const peutGererPaiement = user?.role === 'RECEPTIONIST';
  const peutTelechargerRapport = ['ADMIN', 'MEDECIN', 'LABO_IMAGERIE'].includes(user?.role);

  const charger = useCallback(async () => {
    setErreur('');
    try {
      const data = await getVisiteDetail(id);
      setVisite(data);

      if (data.status === 'DISCHARGED' && peutVoirFacture) {
        try {
          const f = await getFactureParVisite(id);
          setFacture(f);
        } catch {
          setFacture(null);
        }
      }
    } catch (err) {
      setErreur(
        err.response?.status === 404
          ? "Cette visite n'existe pas."
          : 'Erreur lors du chargement du dossier. Vérifiez que le serveur backend est démarré.'
      );
    } finally {
      setChargement(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  useEffect(() => {
    charger();
  }, [charger]);

  useEffect(() => {
    if (notifications.length > dernierNombreNotifs.current) {
      const nouvelles = notifications.slice(0, notifications.length - dernierNombreNotifs.current);
      if (nouvelles.some((n) => String(n.visitId) === String(id))) {
        charger();
      }
    }
    dernierNombreNotifs.current = notifications.length;
  }, [notifications, id, charger]);

  useEffect(() => {
    if (peutAgirMedecin) {
      listerLits().then(setLitsDisponibles).catch(() => {});
    }
  }, [peutAgirMedecin]);

  const consultationCourante = visite?.consultations?.[visite.consultations.length - 1];
  const visiteCloturee = visite?.status === 'DISCHARGED';
  // Un patient hospitalisé est toujours en prise en charge active : le médecin doit
  // pouvoir continuer à prescrire et demander des examens de suivi. Seule une sortie
  // réelle (DISCHARGED) verrouille complètement le dossier.
  const priseEnChargeActive = peutAgirMedecin && visite?.status !== 'DISCHARGED';
  const litsPourService = litsDisponibles.filter((l) => l.service === serviceHospitalisation && !l.occupe);

  async function handleOuvrirConsultation(e) {
    e.preventDefault();
    setErreur('');
    setAction(true);
    try {
      await ouvrirConsultation(id, { diagnostic, planTraitement });
      setDiagnostic('');
      setPlanTraitement('');
      await charger();
    } catch (err) {
      setErreur(err.response?.data?.message || "Erreur lors de l'ouverture de la consultation.");
    } finally {
      setAction(false);
    }
  }

  function ajouterLigneMedicament() {
    setMedicaments([...medicaments, { medicament: '', dosage: '', duree: '', instructions: '' }]);
  }

  function retirerLigneMedicament(index) {
    setMedicaments(medicaments.filter((_, i) => i !== index));
  }

  function modifierLigneMedicament(index, champ, valeur) {
    const copie = [...medicaments];
    copie[index][champ] = valeur;
    setMedicaments(copie);
  }

  async function handleAjouterPrescription(e) {
    e.preventDefault();
    setErreur('');
    setAction(true);
    try {
      const items = medicaments.filter((m) => m.medicament.trim() !== '');
      if (items.length === 0) {
        setErreur('Ajoutez au moins un médicament.');
        setAction(false);
        return;
      }
      await ajouterPrescription(consultationCourante.id, { items });
      setMedicaments([{ medicament: '', dosage: '', duree: '', instructions: '' }]);
      await charger();
    } catch (err) {
      setErreur(err.response?.data?.message || "Erreur lors de l'ajout de la prescription.");
    } finally {
      setAction(false);
    }
  }

  async function handleDemanderExamen(e) {
    e.preventDefault();
    setErreur('');
    setAction(true);
    try {
      await demanderExamen(consultationCourante.id, { type: typeExamen, libelle: libelleExamen });
      setLibelleExamen('');
      await charger();
    } catch (err) {
      setErreur(err.response?.data?.message || "Erreur lors de la demande d'examen.");
    } finally {
      setAction(false);
    }
  }

  async function handleSortie() {
    if (!window.confirm('Confirmer la sortie directe du patient (statut DISCHARGED) ?')) return;
    setErreur('');
    setAction(true);
    try {
      await sortirPatient(id, {});
      await charger();
    } catch (err) {
      setErreur(err.response?.data?.message || 'Erreur lors de la validation de la sortie.');
    } finally {
      setAction(false);
    }
  }

  async function handleHospitalisation(e) {
    e.preventDefault();
    if (!serviceHospitalisation) {
      setErreur("Sélectionnez le service d'hospitalisation.");
      return;
    }
    setErreur('');
    setAction(true);
    try {
      await hospitaliserPatient(id, {
        service: serviceHospitalisation,
        litId: litChoisiId ? Number(litChoisiId) : undefined,
      });
      await charger();
    } catch (err) {
      setErreur(err.response?.data?.message || "Erreur lors de l'hospitalisation.");
    } finally {
      setAction(false);
    }
  }

  async function handleFinHospitalisation() {
    if (!window.confirm('Confirmer la fin du séjour hospitalier ? Le lit sera libéré et la facture générée.')) return;
    setErreur('');
    setAction(true);
    try {
      await finHospitalisation(id);
      await charger();
    } catch (err) {
      setErreur(err.response?.data?.message || 'Erreur lors de la clôture du séjour.');
    } finally {
      setAction(false);
    }
  }

  async function handleTelechargerFacture() {
    setErreur('');
    setTelechargementEnCours('facture');
    try {
      const blob = await telechargerFacturePdf(facture.id);
      declencherTelechargement(blob, `facture-${facture.id}.pdf`);
    } catch (err) {
      const message = await obtenirMessageErreur(err, 'Erreur lors du téléchargement de la facture.');
      setErreur(message);
    } finally {
      setTelechargementEnCours(null);
    }
  }

  async function handleTelechargerRapport(examId) {
    setErreur('');
    setTelechargementEnCours(`examen-${examId}`);
    try {
      const blob = await telechargerRapportExamen(examId);
      declencherTelechargement(blob, `rapport-examen-${examId}.pdf`);
    } catch (err) {
      const message = await obtenirMessageErreur(err, 'Erreur lors du téléchargement du rapport.');
      setErreur(message);
    } finally {
      setTelechargementEnCours(null);
    }
  }

  async function handleMarquerPayee() {
    if (!window.confirm('Confirmer que cette facture a été payée ?')) return;
    setErreur('');
    setAction(true);
    try {
      const factureMiseAJour = await marquerFacturePayee(facture.id);
      setFacture(factureMiseAJour);
    } catch (err) {
      setErreur(err.response?.data?.message || 'Erreur lors de la mise à jour du paiement.');
    } finally {
      setAction(false);
    }
  }

  if (chargement) return <div className="page-container"><p>Chargement du dossier...</p></div>;

  if (erreur && !visite) {
    return (
      <div className="page-container">
        <div className="alert alert-error">{erreur}</div>
        <button className="btn btn-outline" onClick={() => navigate('/attente')}>Retour à la liste</button>
      </div>
    );
  }

  return (
    <div className="page-container">
      <button className="btn btn-outline btn-sm" onClick={() => navigate('/attente')}>← Retour</button>

      <div className="visit-header">
        <h1>{visite.patient.prenom} {visite.patient.nom}</h1>
        <div className="visit-header-badges">
          <UrgencyBadge niveau={visite.niveauUrgence} />
          <StatusBadge status={visite.status} />
        </div>
      </div>

      {erreur && <div className="alert alert-error">{erreur}</div>}

      <div className="card">
        <h2>Informations patient</h2>
        <div className="info-grid">
          <div><span className="info-label">Symptômes :</span> {visite.symptomes}</div>
          <div><span className="info-label">Arrivée :</span> {new Date(visite.heureArrivee).toLocaleString('fr-FR')}</div>
          <div><span className="info-label">Médecin :</span> {visite.medecinNom || 'Non assigné'}</div>
          <div><span className="info-label">Salle :</span> {visite.salleAffectee || '—'}</div>
          {visite.status === 'HOSPITALIZED' && (
            <div><span className="info-label">Lit :</span> {visite.litAffecte || '—'}</div>
          )}
        </div>
      </div>

      {(consultationCourante || (peutAgirMedecin && !visiteCloturee)) && (
        <div className="card">
          <h2>Consultation, diagnostic et plan de traitement</h2>

          {!consultationCourante ? (
            <form className="form-grid" onSubmit={handleOuvrirConsultation}>
              <div className="form-group form-group-full">
                <label>Diagnostic *</label>
                <textarea required rows={2} value={diagnostic} onChange={(e) => setDiagnostic(e.target.value)} />
              </div>
              <div className="form-group form-group-full">
                <label>Plan de traitement *</label>
                <textarea required rows={2} value={planTraitement} onChange={(e) => setPlanTraitement(e.target.value)} />
              </div>
              <div className="form-actions">
                <button type="submit" className="btn btn-primary" disabled={action}>Ouvrir la consultation</button>
              </div>
            </form>
          ) : (
            <div className="consultation-summary">
              <p><span className="info-label">Diagnostic :</span> {consultationCourante.diagnostic}</p>
              <p><span className="info-label">Plan de traitement :</span> {consultationCourante.planTraitement}</p>
              <p className="text-muted">Consultation par {consultationCourante.medecinNom} le {new Date(consultationCourante.dateConsultation).toLocaleString('fr-FR')}</p>
            </div>
          )}
        </div>
      )}

      {consultationCourante && (
        <div className="card">
          <h2>Prescription (Ordonnance)</h2>

          {priseEnChargeActive && (
            <>
              <p className="text-muted">Le prix des médicaments n'est pas géré ici — la facturation ne porte que sur la consultation, les analyses et le séjour.</p>
              <form onSubmit={handleAjouterPrescription}>
                {medicaments.map((m, idx) => (
                  <div className="prescription-row" key={idx}>
                    <input placeholder="Médicament *" value={m.medicament} onChange={(e) => modifierLigneMedicament(idx, 'medicament', e.target.value)} />
                    <input placeholder="Dosage *" value={m.dosage} onChange={(e) => modifierLigneMedicament(idx, 'dosage', e.target.value)} />
                    <input placeholder="Durée" value={m.duree} onChange={(e) => modifierLigneMedicament(idx, 'duree', e.target.value)} />
                    <input placeholder="Instructions" value={m.instructions} onChange={(e) => modifierLigneMedicament(idx, 'instructions', e.target.value)} />
                    {medicaments.length > 1 && (
                      <button type="button" className="btn btn-outline btn-sm" onClick={() => retirerLigneMedicament(idx)}>✕</button>
                    )}
                  </div>
                ))}
                <button type="button" className="btn btn-outline btn-sm" onClick={ajouterLigneMedicament}>+ Ajouter un médicament</button>
                <div className="form-actions">
                  <button type="submit" className="btn btn-primary" disabled={action}>Enregistrer la prescription</button>
                </div>
              </form>
            </>
          )}

          {consultationCourante.prescriptions.length > 0 ? (
            <div className="prescriptions-list">
              <h3>Prescriptions enregistrées</h3>
              {consultationCourante.prescriptions.map((p) => (
                <ul key={p.id} className="prescription-items">
                  {p.items.map((it) => (
                    <li key={it.id}>{it.medicament} — {it.dosage} {it.duree && `(${it.duree})`}</li>
                  ))}
                </ul>
              ))}
            </div>
          ) : (
            <p className="text-muted">Aucune prescription enregistrée.</p>
          )}
        </div>
      )}

      {consultationCourante && (
        <div className="card">
          <h2>Examens complémentaires</h2>

          {priseEnChargeActive && (
            <form className="form-inline" onSubmit={handleDemanderExamen}>
              <select value={typeExamen} onChange={(e) => setTypeExamen(e.target.value)}>
                {TYPES_EXAMEN.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
              <input placeholder="Libellé (ex : NFS, Radio thorax...)" required value={libelleExamen} onChange={(e) => setLibelleExamen(e.target.value)} />
              <button type="submit" className="btn btn-secondary" disabled={action}>Demander</button>
            </form>
          )}

          {consultationCourante.exams.length > 0 ? (
            <table className="table table-compact">
              <thead><tr><th>Type</th><th>Libellé</th><th>Statut</th><th>Résultat</th><th></th></tr></thead>
              <tbody>
                {consultationCourante.exams.map((ex) => (
                  <tr key={ex.id}>
                    <td>{ex.type}</td>
                    <td>{ex.libelle}</td>
                    <td><StatusBadge status={ex.statut} /></td>
                    <td>{ex.resultat || <span className="text-muted">En attente</span>}</td>
                    <td>
                      {ex.statut === 'RESULTATS_DISPONIBLES' && peutTelechargerRapport && (
                        <button
                          className="btn btn-outline btn-sm"
                          disabled={telechargementEnCours === `examen-${ex.id}`}
                          onClick={() => handleTelechargerRapport(ex.id)}
                        >
                          {telechargementEnCours === `examen-${ex.id}` ? 'Téléchargement...' : 'Rapport PDF'}
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p className="text-muted">Aucun examen demandé.</p>
          )}
        </div>
      )}

      {peutAgirMedecin && consultationCourante && !visiteCloturee && visite.status !== 'HOSPITALIZED' && (
        <div className="card card-decision">
          <h2>Décision de sortie ou d'hospitalisation</h2>
          <div className="decision-actions">
            <button className="btn btn-success" disabled={action} onClick={handleSortie}>
              Autoriser la sortie (DISCHARGED)
            </button>

            <form className="form-inline" onSubmit={handleHospitalisation}>
              <select value={serviceHospitalisation} onChange={(e) => { setServiceHospitalisation(e.target.value); setLitChoisiId(''); }}>
                <option value="">Service d'hospitalisation...</option>
                {SERVICES_MEDICAUX.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
              {serviceHospitalisation && (
                <select value={litChoisiId} onChange={(e) => setLitChoisiId(e.target.value)}>
                  <option value="">Assignation automatique</option>
                  {litsPourService.map((l) => (
                    <option key={l.id} value={l.id}>Lit {l.numero}</option>
                  ))}
                </select>
              )}
              {serviceHospitalisation && litsPourService.length === 0 && (
                <span className="text-muted">Aucun lit libre dans ce service actuellement.</span>
              )}
              <button type="submit" className="btn btn-warning" disabled={action}>
                Hospitaliser (HOSPITALIZED)
              </button>
            </form>
          </div>
        </div>
      )}

      {peutAgirMedecin && visite.status === 'HOSPITALIZED' && (
        <div className="card card-decision">
          <h2>Séjour en cours</h2>
          <p className="text-muted">Le patient occupe actuellement {visite.litAffecte}. Vous pouvez continuer à prescrire et demander des examens de suivi ci-dessus tant que le séjour n'est pas terminé.</p>
          <button className="btn btn-success" disabled={action} onClick={handleFinHospitalisation}>
            Terminer l'hospitalisation (libère le lit)
          </button>
        </div>
      )}

      {peutVoirFacture && visite.status === 'DISCHARGED' && (
        <div className="card">
          <h2>Facture</h2>
          {facture ? (
            <>
              <table className="table table-compact">
                <thead><tr><th>Libellé</th><th>Montant (Ar)</th></tr></thead>
                <tbody>
                  {facture.items.map((it) => (
                    <tr key={it.id}><td>{it.libelle}</td><td>{it.montant.toLocaleString('fr-FR')}</td></tr>
                  ))}
                </tbody>
                <tfoot>
                  <tr><td><strong>Total</strong></td><td><strong>{facture.montantTotal.toLocaleString('fr-FR')} Ar</strong></td></tr>
                </tfoot>
              </table>
              <div className="form-inline" style={{ marginTop: '0.75rem' }}>
                <span className={`badge ${facture.statut === 'PAID' ? 'badge-facture-paid' : 'badge-facture-unpaid'}`}>
                  {facture.statut === 'PAID' ? 'Payée' : 'Non payée'}
                </span>
                <button className="btn btn-outline btn-sm" disabled={telechargementEnCours === 'facture'} onClick={handleTelechargerFacture}>
                  {telechargementEnCours === 'facture' ? 'Téléchargement...' : 'Télécharger la facture (PDF)'}
                </button>
                {peutGererPaiement && facture.statut !== 'PAID' && (
                  <button className="btn btn-success btn-sm" disabled={action} onClick={handleMarquerPayee}>
                    Marquer comme payée
                  </button>
                )}
              </div>
            </>
          ) : (
            <p className="text-muted">Facture en cours de génération...</p>
          )}
        </div>
      )}

      {visite.status === 'DISCHARGED' && (
        <div className="alert alert-success">
          Dossier clôturé le {new Date(visite.dateFinHospitalisation || visite.dateSortieUrgences).toLocaleString('fr-FR')}.
        </div>
      )}
    </div>
  );
}