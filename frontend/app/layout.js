import './globals.css';
import Sidebar from '@/components/Sidebar';

export const metadata = {
    title: 'Hospital Roster Management System',
    description: 'Automated shift scheduling for hospital staff — Doctors, Nurses, and Wardens',
};

export default function RootLayout({ children }) {
    return (
        <html lang="en">
            <body>
                <div className="app-layout">
                    <Sidebar />
                    <main className="main-content">
                        {children}
                    </main>
                </div>
            </body>
        </html>
    );
}
