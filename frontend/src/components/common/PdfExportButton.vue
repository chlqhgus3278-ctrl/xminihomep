<template>
  <button type="button" class="pdf-export-button" @click="exportPdf" :disabled="loading">
    {{ loading ? '생성 중...' : '📄 PDF 저장' }}
  </button>
</template>

<script>
import { defineComponent } from 'vue'
import html2pdf from 'html2pdf.js'

export default defineComponent({
  name: 'PdfExportButton',
  props: { username: String },
  data() {
    return { loading: false }
  },
  methods: {
    async exportPdf() {
      this.loading = true
      const el = document.getElementById('portfolio-content')
      try {
        await html2pdf()
          .set({
            margin: 10,
            filename: `${this.username}_portfolio.pdf`,
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { scale: 2, useCORS: true },
            jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }
          })
          .from(el)
          .save()
      } finally {
        this.loading = false
      }
    }
  }
})
</script>
