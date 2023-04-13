<template>
    <div id="item">
        <div id="content" @click="selected">
            <h2>{{ props.tournament.name }}</h2>
            <p>{{ props.tournament.description }}</p>
            <!-- TODO entweder nur game phase oder anzeigen was gerade angezeigt wird -->
            <!-- TODO date an den unteren rand des items -->
            <p v-if="registrationOver">{{props.tournament.game_phase[0].toLocaleDateString()}}
                - {{props.tournament.game_phase[1].toLocaleDateString()}}</p>
            <p v-else>{{props.tournament.registration_phase[0].toLocaleDateString()}}
                - {{props.tournament.registration_phase[1].toLocaleDateString()}}</p>
        </div>
        <font-awesome-icon v-if="props.canCreate"
                           id="settings"
                           :icon="['fas', 'gear']" class="fa-2x" @click="settings">
        </font-awesome-icon>
        <font-awesome-icon v-if="props.canCreate && !props.tournament.visible"
                           id="invisible" :icon="['fas', 'eye-slash']" class="fa-2x">
        </font-awesome-icon>
    </div>
</template>

<script setup lang="ts">
import {Tournament} from "@/interfaces/tournament"

const props = defineProps<{
    canCreate: boolean,
    tournament: Tournament
    }>()

const registrationOver =  new Date() >= props.tournament.registration_phase[1]

const emit = defineEmits(['selected', 'settings'])

function selected() {
    emit('selected', props.tournament.name);
}

function settings() {
    emit('settings', props.tournament.name);
}
</script>

<style scoped>
#item {
    border-radius: 15px;
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
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
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
    max-width: calc(100% - 100px);
}

#content:hover {
    cursor: pointer;
}

#item:hover {
    box-shadow: 0 0 10px black;
}

#content:active {
    background-color: #C0C0C0;
}

@media only screen and (max-width: 900px) {
    #item {
        width: 100%;
        min-height: 100px;
        height: auto;
    }
}
</style>