<template>
  <div id="item">
    <div id="content" @click="selected">
      <h2>{{ name }}</h2>
      <p>{{ description }}</p>
    </div>
    <div id="type">
      <font-awesome-icon v-if="type==='GROUPS'" :icon="['fas', 'table-cells-large']" class="fa-2x"></font-awesome-icon>
      <font-awesome-icon v-else-if="type==='KNOCKOUT'" :icon="['fas', 'user-group']" class="fa-2x"></font-awesome-icon>
    </div>
    <font-awesome-icon v-if="canEdit"
                       id="settings"
                       :icon="['fas', 'gear']" class="fa-2x" @click="settings">
    </font-awesome-icon>
  </div>
</template>

<script setup>

const props = defineProps({
  name: String,
  description: String,
  type: String,
  canEdit: Boolean
});

const emit = defineEmits(['selected', 'settings']);

function selected() {
  emit('selected', props.name);
}

function settings() {
  console.log("settings");
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

#type {
  position: absolute;
  left: 10px;
  bottom: 10px;
  color: #303030;
}

h2 {
  text-align: center;
}

p {
  text-align: center;
}

#item:hover {
  box-shadow: 0 0 10px black;
  cursor: pointer;
}

#item:active {
  background-color: #C0C0C0;
}

#settings {
  position: absolute;
  right: 10px;
  bottom: 10px;
  color: #303030;
}

#settings:hover {
  filter: drop-shadow(0 0 10px #808080);
}

#settings:active {
  color: #404040;
}
</style>