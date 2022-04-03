<script setup lang="ts">
import {ref} from 'vue'

const string = [];
let id = 0
let correctInput = false


const newEntry = ref('')
const entries = ref([
  {id: id++, text: 'Learn Vue'}
])

function checkInput(i: { target: { value: string; }; }) {
  newEntry.value = i.target.value.replace(/(\w)(\w*)/g,
      function (g0, g1, g2) {
        return g1.toUpperCase() + g2.toLowerCase();
      });

  const temp = i.target.value.trim().split(' ')
  let specialCharacter = i.target.value.match(/[^a-z]/i)
  correctInput = temp.length === 2 && (specialCharacter === null || specialCharacter[0] === " ")
}


function addEntry() {
  if (correctInput) {
    entries.value.push({id: id++, text: newEntry.value})
    newEntry.value = ''
    correctInput = false
  }
}

// function removeEntry(newName: { id: number; text: string; }) {
//   entries.value = entries.value.filter((t) => t !== newName)
// }
</script>

<template>
  <form @submit.prevent="addEntry">
    <input :value="text" @input="checkInput" placeholder="Vorname Nachname">
    <button>Teilnehmer hinzufügen</button>
  </form>
  <ul>
    <li v-for="name in entries" :key="name.id">
      {{ name.text }}
      <!--      <button @click="removeEntry(name)">X</button>-->
    </li>
  </ul>
</template>

<style scoped>
header {
  height: 100px;
  width: 100vw;
  background-color: red;
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: space-between;
}
</style>
