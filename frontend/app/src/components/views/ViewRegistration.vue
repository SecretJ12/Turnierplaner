<script setup lang="ts">
import {ref, type Ref} from 'vue'

const string = [];
let id = 0
let correctInput = false

const props = defineProps({
  idTour: String,
  idComp: String
})

const input = ref('')
const entries: Ref<String[]> = ref([])

function checkInput(i: any ) {
  input.value = i.target.value
  const tmp = input.value.trim().split(' ');
  let specialCharacter = input.value.match(/[^a-z]/i)
  correctInput = tmp.length === 2 && (specialCharacter === null || specialCharacter[0] === " ")
}


function addEntry() {
  if (correctInput) {
    const entry = input.value.replace(/(\w)(\w*)/g,
        function (g0, g1, g2) {
          return g1.toUpperCase() + g2.toLowerCase();
        })

    if (entries.value.includes(entry)) {
      alert("Name existiert bereits")
      correctInput = false
    } else {
      entries.value.push(entry)
      input.value = ""
      correctInput = false
    }
  }
}

// function removeEntry(newName: { id: number; text: string; }) {
//   entries.value = entries.value.filter((t) => t !== newName)
// }
</script>

<template>
  <h2>{{ props.idTour }} - {{ props.idComp }}</h2>
  <form @submit.prevent="addEntry">
    <input :value="input"  @input="checkInput" placeholder="Vorname Nachname">
    <button>Teilnehmer hinzufügen</button>
  </form>
  <ul>
    <li v-for="name in entries" >
      {{ name }}
      <!--      <button @click="removeEntry(name)">X</button>-->
    </li>
  </ul>


</template>

<style scoped>
h2 {
  text-align: center;
}
</style>
