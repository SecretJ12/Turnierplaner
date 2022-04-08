<script>

</script>

<template>
  <div id="headerRight">
    <font-awesome-icon :icon="['fas', 'right-to-bracket']" class="fa-2x" />
  </div>
</template>

<style>
  #headerRight {
    padding-right: 10px;
    display: flex;
    align-items: center;
    align-content: flex-end;
    flex-direction: row;
    justify-content: flex-end;
  }

  #headerRight > * {
    margin-right: 20px;
    flex-grow: 0;
    flex-shrink: 1;
  }
</style>