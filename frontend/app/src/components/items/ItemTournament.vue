<template>
  <div id="item">
    <div id="content" @click="selected">
      <h2>{{ name }}</h2>
      <p>{{ description }}</p>
    </div>
    <font-awesome-icon v-if="canCreate"
                       id="settings"
                       :icon="['fas', 'gear']" class="fa-2x" @click="settings">
    </font-awesome-icon>
    <font-awesome-icon v-if="canCreate && !visible"
                       id="invisible" :icon="['fas', 'eye-slash']" class="fa-2x">
    </font-awesome-icon>
  </div>
</template>

<script setup>

const props = defineProps({
  name: String,
  description: String,
  beginRegistration: Date,
  endRegistration: Date,
  beginGamePhase: Date,
  endGamePhase: Date,
  visible: Boolean,
  canCreate: Boolean
})

const emit = defineEmits(['selected', 'settings']);

function selected() {
  emit('selected', props.name);
}

function settings() {
  emit('settings', props.name);
}
</script>

<style scoped>
#item {
  border-radius: 10px;
  padding: 0;
  width: 420px;
  height: 300px;
  position: relative;
  overflow: hidden;
}

#content {
  padding: 20px 10px 0 10px;
  background-color: #D0D0D0;
  box-shadow: 0 0 5px #909090;
  width: 400px;
  height: 400px;
}

#settings {
  position: absolute;
  right: 10px;
  bottom: 10px;
  color: #303030;
}

#invisible {
  position: absolute;
  left: 10px;
  bottom: 10px;
  color: #303030;
}

#settings:hover {
  filter: drop-shadow(0 0 10px #808080);
}

#settings:active {
  color: #404040;
}

h2 {
  margin-top: 0;
  text-align: center;
  overflow-wrap: break-word;
}

p {
  text-align: center;
  overflow-wrap: break-word;
}

#item:hover {
  box-shadow: 0 0 10px black;
  cursor: pointer;
}

#content:active {
  background-color: #C0C0C0;
}
</style>