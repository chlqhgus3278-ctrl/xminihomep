<template>
  <router-view />
  <AppDialog />
  <AppToast />
</template>

<script>
import { defineComponent } from 'vue'
import AppDialog from './components/common/AppDialog.vue'
import AppToast from './components/common/AppToast.vue'

export default defineComponent({
  name: 'App',
  components: { AppDialog, AppToast }
})
</script>
