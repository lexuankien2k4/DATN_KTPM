import { createApp } from 'vue'
import { createPinia } from 'pinia' //
import App from './App.vue'
import router from './router' //
import './assets/css/styles.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'
// 1. Khởi tạo ứng dụng Vue
const app = createApp(App)

// 2. Khởi tạo Pinia (Kho lưu trữ trạng thái)
const pinia = createPinia()

// 3. Đăng ký Pinia vào ứng dụng
// RẤT QUAN TRỌNG: Phải .use(pinia) TRƯỚC .use(router)
app.use(pinia) 

// 4. Đăng ký Router
app.use(router)

// 5. Gắn ứng dụng vào phần tử HTML có id="app"
app.mount('#app')