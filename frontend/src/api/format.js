export const money = n => '₹' + Number(n).toLocaleString('en-IN', { maximumFractionDigits: 2 });
export const shortId = id => String(id || '').slice(-6).toUpperCase();
export const STATUS = { PLACED: 'Placed', PREPARING: 'Preparing', OUT_FOR_DELIVERY: 'Out for delivery', DELIVERED: 'Delivered', CANCELLED: 'Cancelled' };
export const STEPS = ['PLACED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED'];
export const ACTIVE = ['PLACED', 'PREPARING', 'OUT_FOR_DELIVERY'];
export const formatDate = v => v ? new Date(v).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' }) : '';
