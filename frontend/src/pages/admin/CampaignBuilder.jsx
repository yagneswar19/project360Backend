import React, { useState } from 'react'
import api from '../../api/client'

export default function CampaignBuilder() {
  const [c, setC] = useState({
    title: '', discountType: 'Percentage', category: 'Electronics',
    imageUrl: '', costPoints: 0, startDate: '', endDate: '', description: '', addToOffers: false
  })
  const [msg, setMsg] = useState('')
  const onChange = e => {
    const { name, value, type, checked } = e.target
    setC(p => ({ ...p, [name]: type === 'checkbox' ? checked : value }))
  }

  // ...existing code... 
  const submit = async e => {
    e.preventDefault()
    try {
      // create campaign 
      const { data: campaign } = await api.post('/admin/campaigns', c)
      let newMsg = 'Saved campaign #' + campaign.id

      // if checkbox checked, also add to offers 
      if (c.addToOffers) {
        const offerPayload = {
          title: c.title,
          category: c.category,
          description: c.description,
          costPoints: Number(c.costPoints) || 0,
          imageUrl: c.imageUrl || '',
          active: true
        }
        try {
          const { data: offer } = await api.post('/admin/offers', offerPayload)
          newMsg += ` — added offer #${offer.id}`
        } catch (offerErr) {
          // non-fatal: report offer creation error but keep campaign saved 
          const reason = offerErr?.response?.data || offerErr.message || String(offerErr)
          newMsg += ` — failed to add offer (${reason})`
        }
      }

      setMsg(newMsg)
    } catch (err) {
      const detail = err?.response?.data || err.message || String(err)
      setMsg('Error: ' + detail)
    }
  }
  // ...existing code... 

  return (
    <div className="card" style={{ maxWidth: 720 }}>
      <h3>Campaign Builder</h3>
      <form onSubmit={submit} className="grid cols-2">
        <div><label>Title</label><input className="input" name="title" value={c.title}
          onChange={onChange} required /></div>
        <div><label>Discount Type</label><select className="input"
          name="discountType" value={c.discountType}
          onChange={onChange}><option>Percentage</option><option>Flat</option></select>
        </div>
        <div><label>Category</label><select className="input" name="category"
          value={c.category}
          onChange={onChange}><option>Electronics</option><option>Travel</option><option
          >Groceries</option><option>Lifestyle</option></select></div>
        <div><label>Image URL (optional)</label><input className="input"
          name="imageUrl" value={c.imageUrl} onChange={onChange} /></div>
        <div><label>Cost (points)</label><input className="input" type="number"
          name="costPoints" value={c.costPoints} onChange={onChange} /></div>
        <div><label>Start Date</label><input className="input" type="date"
          name="startDate" value={c.startDate} onChange={onChange} /></div>
        <div><label>End Date</label><input className="input" type="date"
          name="endDate" value={c.endDate} onChange={onChange} /></div>
        <div style={{ gridColumn: '1/-1' }}><label>Description</label><textarea
          className="input" name="description" value={c.description}
          onChange={onChange} /></div>
        <div><label><input type="checkbox" name="addToOffers"
          checked={c.addToOffers} onChange={onChange} /> Add to Offers page after
          launch</label></div>
        <div style={{ gridColumn: '1/-1' }}><button className="button">Next:
          Audience</button></div>
      </form>
      {msg && <div className="badge" style={{ marginTop: 8 }}>{msg}</div>}
    </div>
  )
} 
