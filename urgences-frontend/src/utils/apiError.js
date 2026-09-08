export async function obtenirMessageErreur(err, messageParDefaut) {
    const data = err?.response?.data;
  
    if (data instanceof Blob) {
      try {
        const texte = await data.text();
        const json = JSON.parse(texte);
        return json.message || messageParDefaut;
      } catch {
        return messageParDefaut;
      }
    }
  
    return err?.response?.data?.message || messageParDefaut;
  }