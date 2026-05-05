import { defineStore } from 'pinia'

// Định nghĩa store để quản lý trạng thái chat
export const useChatStore = defineStore('chat', {
  state: () => ({
    // Lưu danh sách tin nhắn để không bị mất khi chuyển trang
    messages: [
      { 
        type: 'ai', 
        text: 'Chào bạn! Tôi có thể giúp gì cho bạn trong việc chọn lựa chiếc xe VinFast ưng ý nhất?' 
      }
    ]
  }),
  
  actions: {
    // Hàm thêm tin nhắn mới vào lịch sử
    addMessage(msg) {
      this.messages.push(msg)
    },
    
    // Hàm xóa lịch sử chat nếu cần
    clearChat() {
      this.messages = [
        { type: 'ai', text: 'Chào bạn! Tôi có thể giúp gì cho bạn...' }
      ]
    }
  },
  
 
})