'use client';

import { useState, useEffect } from 'react';

export default function VisitorLog({ apiBase }) {
  const [visitors, setVisitors] = useState([]);
  const [aiSummary, setAiSummary] = useState('');
  const [showAiBox, setShowAiBox] = useState(false);
  const [loadingAi, setLoadingAi] = useState(false);

  // Ambil Data Log Awal
  useEffect(() => {
    async function fetchInitialLogs() {
      try {
        const res = await fetch(`${apiBase}/logs`);
        const data = await res.json();
        setVisitors(data);
      } catch (err) {
        console.error("Gagal mengambil data log:", err);
      }
    }
    fetchInitialLogs();
  }, [apiBase]);

  // Dengarkan koneksi Pipa Real-time (SSE)
  useEffect(() => {
    const eventSource = new EventSource(`${apiBase}/stream`);

    eventSource.onmessage = (event) => {
      const newVisitor = JSON.parse(event.data);
      setVisitors((prevVisitors) => [newVisitor, ...prevVisitors]);
    };

    eventSource.onerror = (err) => {
      console.error("Koneksi real-time terputus:", err);
      eventSource.close();
    };

    return () => eventSource.close();
  }, [apiBase]);

  // Fungsi Request Analisis Gemini AI
  const handleGenerateAiSummary = async () => {
    setLoadingAi(true);
    setShowAiBox(true);
    setAiSummary('Sedang menganalisis log dengan Gemini AI 2.5 Flash...');
    
    try {
      const res = await fetch(`${apiBase}/ai-summary`);
      const text = await res.text();
      setAiSummary(text);
    } catch (err) {
      setAiSummary('[Sistem]: Gagal memuat analisis AI.');
    } finally {
      setLoadingAi(false);
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-xl border border-gray-700">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold text-white">Real-time Log Kunjungan</h2>
        <button onClick={handleGenerateAiSummary} disabled={loadingAi} className="bg-purple-600 hover:bg-purple-700 text-white font-medium text-xs px-4 py-2 rounded transition disabled:opacity-50">
          {loadingAi ? 'Menganalisis...' : '✨ Generate Summary AI'}
        </button>
      </div>

      {showAiBox && (
        <div className="bg-purple-950/40 border border-purple-800 text-purple-200 p-4 rounded-lg mb-4 text-sm">
          {aiSummary}
        </div>
      )}

      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead className="bg-gray-700 text-gray-300 uppercase text-xs">
            <tr>
              <th className="p-3">Foto</th>
              <th className="p-3">Nama / NIK</th>
              <th className="p-3">Tujuan</th>
              <th className="p-3">Waktu</th>
              <th className="p-3 text-center">Risk Score</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-700">
            {visitors.length === 0 ? (
              <tr>
                <td colSpan="5" className="p-4 text-center text-gray-500">Belum ada tamu hari ini.</td>
              </tr>
            ) : (
              visitors.map((v) => {
                let badgeColor = 'bg-emerald-500/20 text-emerald-400 border-emerald-500/50';
                if (v.statusRisiko === 'YELLOW') badgeColor = 'bg-amber-500/20 text-amber-400 border-amber-500/50';
                if (v.statusRisiko === 'RED') badgeColor = 'bg-rose-500/20 text-rose-400 border-rose-500/50';

                const jam = new Date(v.waktuMasuk).toLocaleTimeString('id-ID', { hour: '2-digit', minute: '2-digit' });

                return (
                  <tr key={v.id} className="hover:bg-gray-750 transition">
                    <td className="p-3">
                      <img src={v.fotoUrl} alt={v.nama} className="w-10 h-10 rounded-full border border-gray-600 object-cover" />
                    </td>
                    <td className="p-3">
                      <div className="font-medium text-white">{v.nama}</div>
                      <div className="text-xs text-gray-400">{v.nik}</div>
                    </td>
                    <td className="p-3 text-gray-300">{v.tujuan}</td>
                    <td className="p-3 text-gray-400">{jam} WIB</td>
                    <td className="p-3 text-center">
                      <span className={`px-2.5 py-1 rounded-full text-xs font-semibold border ${badgeColor}`}>
                        {v.statusRisiko}
                      </span>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}