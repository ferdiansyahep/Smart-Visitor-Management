import Header from '../app/components/Header';
import RegistrationForm from './components/RegisterForm';
import VisitorLog from './components/VisitLog';

const API_BASE = 'http://localhost:8080/api/visitors';

export default function Home() {
  return (
    <div className="bg-gray-900 text-gray-100 min-h-screen font-sans">
      <div className="container mx-auto p-6">
        
        {/* 1. Pemanggilan Komponen Header */}
        <Header />

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          
          {/* 2. Pemanggilan Komponen Form Input */}
          <RegistrationForm apiBase={API_BASE} />

          {/* 3. Pemanggilan Komponen Tabel Real-time */}
          <div className="lg:col-span-2">
            <VisitorLog apiBase={API_BASE} />
          </div>

        </div>
      </div>
    </div>
  );
}