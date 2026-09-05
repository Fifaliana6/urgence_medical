import { useState, useEffect } from 'react'
import { useLocation } from 'react-router-dom'
import { Search, Receipt, CreditCard } from 'lucide-react'
import api from '../api/client'
import Badge from '../components/Badge'
import { Card, PageHeader, EmptyState, ErrorBanner, Button, Input, Spinner } from '../components/Primitives'
import { INVOICE_STATUS_META, formatCurrency, formatDateTime } from '../lib/constants'

export default function InvoicesPage() {
  const location = useLocation()
  const [visitId, setVisitId] = useState(location.state?.visitId ?? '')
  const [invoice, setInvoice] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [searched, setSearched] = useState(false)

  async function lookup(idToLookup) {
    if (!idToLookup) return
    setLoading(true)
    setError(null)
    setSearched(true)
    try {
      const { data } = await api.get(`/invoices/visite/${idToLookup}`)
      setInvoice(data)
    } catch {
      setInvoice(null)
      setError("Aucune facture trouvée pour cette visite. Elle n'a peut-être pas encore été clôturée.")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (location.state?.visitId) lookup(location.state.visitId)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  async function markPaid() {
    await api.put(`/invoices/${invoice.id}/payer`)
    lookup(visitId)
  }

  return (
    <div className="mx-auto max-w-2xl">
      <PageHeader
        title="Facturation"
        description="Recherchez la facture générée automatiquement à la sortie ou l'hospitalisation d'un patient."
      />

      <Card className="mb-6 p-4">
        <form
          onSubmit={(e) => {
            e.preventDefault()
            lookup(visitId)
          }}
          className="flex gap-2"
        >
          <Input
            placeholder="ID de la visite (ex : 3)"
            value={visitId}
            onChange={(e) => setVisitId(e.target.value)}
            className="flex-1"
          />
          <Button type="submit" variant="secondary" disabled={loading}>
            <Search className="h-4 w-4" />
            Rechercher
          </Button>
        </form>
      </Card>

      {loading && <Spinner />}
      <ErrorBanner message={error} />

      {!loading && searched && !invoice && !error && (
        <Card><EmptyState icon={Receipt} title="Aucune facture" /></Card>
      )}

      {invoice && (
        <Card className="p-5">
          <div className="mb-4 flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-ink">Facture #{invoice.id}</p>
              <p className="text-xs text-ink-soft">Émise le {formatDateTime(invoice.dateEmission)}</p>
            </div>
            <Badge meta={INVOICE_STATUS_META[invoice.statut]} />
          </div>

          <ul className="divide-y divide-border border-y border-border">
            {invoice.lignes?.map((l) => (
              <li key={l.id} className="flex items-center justify-between py-2.5 text-sm">
                <span className="text-ink">{l.description}</span>
                <span className="font-mono text-ink-soft">{formatCurrency(l.montant)}</span>
              </li>
            ))}
          </ul>

          <div className="mt-4 flex items-center justify-between">
            <span className="text-sm font-semibold text-ink">Total</span>
            <span className="font-mono text-lg font-semibold text-ink">
              {formatCurrency(invoice.montantTotal)}
            </span>
          </div>

          {invoice.statut !== 'PAYEE' && (
            <Button onClick={markPaid} className="mt-5 w-full">
              <CreditCard className="h-4 w-4" />
              Marquer comme payée
            </Button>
          )}
        </Card>
      )}
    </div>
  )
}
