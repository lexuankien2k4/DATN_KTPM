import { createRouter, createWebHistory } from 'vue-router'

// --- 1. IMPORT CÁC VIEW CÔNG KHAI ---
import HomeView from '../views/HomeView.vue'
import InstallmentView from '../views/InstallmentView.vue'
import ConsultationForm from '@/views/ConsultationForm.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import PaymentResult from '../views/PaymentResult.vue'
import ProductDetail from '../views/ProductDetail.vue'
import ChatConsultation from '../views/ChatConsultation.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'

// QUAN TRỌNG: Đảm bảo import đúng tên bạn dùng ở dưới
import PurchaseWizard from '../views/PurchaseWizard.vue' 
import ContractSigning from '../views/ContractSigning.vue' 

// Nếu bạn muốn giữ lại trang Deposit cũ để dùng cho việc khác, hãy import riêng:
import DepositView from '../views/DepositView.vue'

// --- 2. IMPORT CÁC VIEW ADMIN ---
import Dashboard from '../views/Dashboard.vue' // Lỗi của bạn nằm ở dòng này nếu bị thiếu
import CarManager from '../views/CarManager.vue'
import EmployeeManager from '../views/EmployeeManager.vue'

// Sử dụng Lazy Loading cho các trang quản lý nặng để tối ưu
const CustomerManager = () => import('../views/CustomerManager.vue')
const ConsultationManager = () => import('../views/ConsultationManager.vue')
const OrderManager = () => import('../views/OrderManager.vue')
const ContractManager = () => import('../views/ContractManager.vue')
const NhatkyManager = () => import('../views/NhatKyManager.vue')
const CarStockManager = () => import('../views/CarStockManager.vue')

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/product/:id', name: 'ProductDetail', component: ProductDetail, props: true },
  { path: '/chat-consultation', name: 'ChatConsultation', component: ChatConsultation },
  
  // LUỒNG MUA TRẢ GÓP 3 BƯỚC CỦA BẠN
{ 
    path: '/deposit', 
    name: 'PurchaseWizard', 
    component: PurchaseWizard,
    meta: { hideHeaderFooter: true },
    props: route => ({ carId: route.query.id }) 
  },

  {
    path: '/contract/sign/:id',
    name: 'ContractSigning',
    component: ContractSigning,
    meta: { hideHeaderFooter: true },
    props: true
  },
  {
    path: '/payment-callback',
    name: 'PaymentResult',
    component: PaymentResult,
    meta: { hideHeaderFooter: true } 
  },

  { path: '/installment', name: 'installment', component: InstallmentView },
  { path: '/consultation', name: 'ConsultationForm', component: ConsultationForm },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },

  // --- HỆ THỐNG QUẢN TRỊ (ADMIN) ---
  {
    path: '/admin',
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, hideHeaderFooter: true },
    children: [
      { path: 'dashboard', name: 'Dashboard', component: Dashboard },
      { path: 'cars', name: 'CarManager', component: CarManager },
      { path: 'employee', name: 'EmployeeManager', component: EmployeeManager },
      { path: 'customers', name: 'CustomerManager', component: CustomerManager },
      { path: 'consultations', name: 'ConsultationManager', component: ConsultationManager },
      { path: 'orders', name: 'OrderManager', component: OrderManager },
      { path: 'contracts', name: 'ContractManager', component: ContractManager },
      { path: 'nhatky', name: 'NhatkyManager', component: NhatkyManager },
      {
        path: 'stocks',
        name: 'CarStocks',
        component: CarStockManager
      },
      {
    path: '/admin/dashboard2',
    name: 'AdminDashboard',
    component: AdminDashboard,
  },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

export default router