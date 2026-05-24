'use client';

import { useState } from 'react';

export default function RegistrationForm({ apiBase }) {
  const [nama, setNama] = useState('');
  const [nik, setNik] = useState('');
  const [tujuan, setTujuan] = useState('');
  const [fotoUrl, setFotoUrl] = useState('https://images.unsplash.com/photo-1535713875002-d1d0cf377fde');

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = { nama, nik, tujuan, fotoUrl };

    try {
      await fetch(`${apiBase}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      // Reset Form isi ketikan setelah sukses kirim
      setNama('');
      setNik('');
      setTujuan('');
    } catch (err) {
      console.error("Gagal mendaftarkan tamu:", err);
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-xl border border-gray-700 h-fit">
      <h2 className="text-xl font-semibold text-white mb-4">Smart Registration</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-xs uppercase tracking-wider text-gray-400 mb-1">Nama Lengkap</label>
          <input type="text" value={nama} onChange={(e) => setNama(e.target.value)} required className="w-full bg-gray-700 border border-gray-600 rounded px-3 py-2 text-white focus:outline-none focus:border-emerald-500" />
        </div>
        <div>
          <label className="block text-xs uppercase tracking-wider text-gray-400 mb-1">NIK (KTP)</label>
          <input type="text" value={nik} onChange={(e) => setNik(e.target.value)} required className="w-full bg-gray-700 border border-gray-600 rounded px-3 py-2 text-white focus:outline-none focus:border-emerald-500" />
        </div>
        <div>
          <label className="block text-xs uppercase tracking-wider text-gray-400 mb-1">Tujuan Kunjungan</label>
          <input type="text" value={tujuan} onChange={(e) => setTujuan(e.target.value)} required className="w-full bg-gray-700 border border-gray-600 rounded px-3 py-2 text-white focus:outline-none focus:border-emerald-500" />
        </div>
        <div>
          <label className="block text-xs uppercase tracking-wider text-gray-400 mb-1">Foto URL (Mock)</label>
          <input type="text" value={fotoUrl} onChange={(e) => setFotoUrl(e.target.value)} className="w-full bg-gray-700 border border-gray-600 rounded px-3 py-2 text-gray-400 text-sm focus:outline-none" />
        </div>
        <button type="submit" className="w-full bg-emerald-500 hover:bg-emerald-600 text-gray-950 font-bold py-2.5 rounded transition">
          Daftar Tamu
        </button>
      </form>
    </div>
  );
}