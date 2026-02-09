// PROMOTIONS.JSX 
// JUST CHANGE THE PORT I KEPT DEFAULT PORT 8080 
import React, { useState } from "react"; 
import { Link } from "react-router-dom"; 
 
export default function Promotions() { 
  const [analytics, setAnalytics] = useState([]); 
  const [loading, setLoading] = useState(false); 
  const [error, setError] = useState(null); 
 
  const getApiBase = () => { 
    if (typeof window !== "undefined" && window.REACT_APP_API_URL) { 
      return String(window.REACT_APP_API_URL).replace(/\/$/, ""); 
    } 
    if (typeof process !== "undefined" && process.env && 
process.env.REACT_APP_API_URL) { 
      return String(process.env.REACT_APP_API_URL).replace(/\/$/, ""); 
    } 
    return "http://localhost:8080"; 
  }; 
 
  async function fetchAnalytics() { 
    setLoading(true); 
    setError(null); 
    try { 
      const base = getApiBase(); 
      const url = `${base}/api/admin/analytics`; 
 
      const headers = { Accept: "application/json" }; 
      const token = 
        localStorage.getItem("token") || 
        localStorage.getItem("accessToken") || 
        sessionStorage.getItem("token"); 
      if (token) headers.Authorization = `Bearer ${token}`; 
 
      const res = await fetch(url, { headers }); 
 
      if (res.status === 401 || res.status === 403) { 
        throw new Error(`Unauthorized (status ${res.status}). Ensure you are logged in as 
ADMIN.`); 
      } 
      if (!res.ok) { 
        const text = await res.text(); 
        throw new Error(`API error ${res.status}: ${text.slice(0, 300)}`); 
      } 
 
      const ct = (res.headers.get("content-type") || "").toLowerCase(); 
      if (!ct.includes("application/json")) { 
        const text = await res.text(); 
        throw new Error("Expected JSON but received non-JSON response: " + text.slice(0, 
300)); 
      } 
 
      const data = await res.json(); 
      const list = Array.isArray(data) ? data : Array.isArray(data.offers) ? data.offers : []; 
      const normalized = list.map((o) => ({ 
        id: o.id ?? o._id ?? null, 
        title: o.title ?? "", 
        category: o.category ?? "", 
        costPoints: o.costPoints ?? o.cost_points ?? 0, 
      })); 
      setAnalytics(normalized); 
    } catch (err) { 
      setError(err?.message || "Unknown error"); 
      setAnalytics([]); 
    } finally { 
      setLoading(false); 
    } 
  } 
 
  return ( 
    <> 
      <div className="grid cols-3"> 
        <div className="card"> 
          <h3>Create Campaign</h3> 
          <p> 
            Quickly design and launch a new promotional campaign. 
             
          </p> 
          <Link className="button" to="/admin/campaigns/new"> 
            Create Campaign 
          </Link> 
        </div> 
 
        <div className="card"> 
          <h3>Analytics</h3> 
          <p>High-level metrics of ongoing campaigns.</p> 
 
          <button className="button" onClick={fetchAnalytics}> 
            OPEN ANALYTICS 
          </button> 
 
          {loading && <div style={{ marginTop: 8 }}>Loading analytics...</div>} 
          {error && <div style={{ marginTop: 8, color: "red" }}>Error: {error}</div>} 
        </div> 
 
        <div className="card"> 
          <h3>View All Offers</h3> 
          <p>Access all available offers in one view. 
           </p> 
          <Link className="button" to="/admin/offers"> 
             DISPLAY OFFERS  
          </Link> 
        </div> 
      </div> 
 
      {/* table rendered below the cards */} 
      <div style={{ marginTop: 20 }}> 
        {analytics.length > 0 ? ( 
          <div style={{ overflowX: "auto" }}> 
            <table style={{ width: "100%", borderCollapse: "collapse" }}> 
              <thead> 
                <tr> 
                  <th style={{ textAlign: "left", borderBottom: "1px solid #ddd", padding: 8 
}}>ID</th> 
                  <th style={{ textAlign: "left", borderBottom: "1px solid #ddd", padding: 8 
}}>Title</th> 
                  <th style={{ textAlign: "left", borderBottom: "1px solid #ddd", padding: 8 
}}>Category</th> 
                  <th style={{ textAlign: "left", borderBottom: "1px solid #ddd", padding: 8 }}>Cost 
Points</th> 
                </tr> 
              </thead> 
              <tbody> 
                {analytics.map((a) => ( 
                  <tr key={a.id ?? `${a.title}-${a.costPoints}`}> 
                    <td style={{ padding: 8, borderBottom: "1px solid #f0f0f0" }}>{a.id}</td> 
                    <td style={{ padding: 8, borderBottom: "1px solid #f0f0f0" }}>{a.title}</td> 
                    <td style={{ padding: 8, borderBottom: "1px solid #f0f0f0" }}>{a.category}</td> 
                    <td style={{ padding: 8, borderBottom: "1px solid #f0f0f0" 
}}>{a.costPoints}</td> 
                  </tr> 
                ))} 
              </tbody> 
            </table> 
          </div> 
        ) : ( 
          // show nothing or a hint if empty and not loading/error 
          !loading && !error && <div style={{ color: "#666" }}>No analytics to show. Click 
"OPEN ANALYTICS".</div> 
        )} 
      </div> 
    </> 
  ); 
}