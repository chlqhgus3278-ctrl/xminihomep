<template>
  <router-view />
  <AppDialog />
</template>

<script>
import { defineComponent } from 'vue'
import AppDialog from './components/common/AppDialog.vue'

export default defineComponent({
  name: 'App',
  components: { AppDialog }
})
</script>
