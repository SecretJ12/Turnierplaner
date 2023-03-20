<template>
  <div id="container">
    <h2>
      {{$t('general.success')}}
    </h2>
    <p>
      {{$t('ViewPlayerRegistration.after')}}
    </p>
  </div>
</template>

<script>
export default {
  name: "ViewAfterPlayerRegistration"
}
</script>

<style scoped>
#container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

</style>