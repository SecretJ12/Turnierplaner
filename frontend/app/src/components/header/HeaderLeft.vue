<template>
  <div id="headerLeft" @click="home">
    <font-awesome-icon :icon="['fas', 'flag']" class="fa-2x" id="colorIcon"></font-awesome-icon>
    <h1 id="colorHeadLine">{{ $t("title") }}</h1>
  </div>
</template>

<script setup>
import {router} from '@/main'

function home() {
  router.replace({path: "/"})
}
</script>

<style scoped>
#headerLeft {
  padding-left: 10px;
  display: flex;
  flex-direction: row;
  align-items: center;
  cursor: pointer;
}

#colorIcon{
  color: #3bb0ff;
}

#colorHeadLine{
  color: #044154;
}

#headerLeft > * {
  margin-left: 20px;
}

h1 {
    font-weight: 900;
}

@media only screen and (max-width: 750px) {
    h1 {
        font-size: 1.0em;
    }
}
</style>