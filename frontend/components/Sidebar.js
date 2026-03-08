'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

const navItems = [
    { href: '/', label: 'Dashboard', icon: '📊' },
    { href: '/staff', label: 'Staff Directory', icon: '👥' },
    { href: '/leaves', label: 'Leave Management', icon: '📅' },
];

export default function Sidebar() {
    const pathname = usePathname();

    return (
        <aside className="sidebar">
            <div className="sidebar-brand">
                <div className="sidebar-brand-icon">🏥</div>
                <h1>Hospital Roster</h1>
            </div>
            <nav className="sidebar-nav">
                {navItems.map((item) => (
                    <Link
                        key={item.href}
                        href={item.href}
                        className={`sidebar-link ${pathname === item.href ? 'active' : ''}`}
                    >
                        <span style={{ fontSize: '18px' }}>{item.icon}</span>
                        {item.label}
                    </Link>
                ))}
            </nav>
            <div style={{ padding: '16px 20px', borderTop: '1px solid var(--border-color)' }}>
                <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                    Hospital Roster v1.0
                </div>
            </div>
        </aside>
    );
}
