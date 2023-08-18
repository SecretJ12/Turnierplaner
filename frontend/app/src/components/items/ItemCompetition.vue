<template>
	<div id="item">
		<div id="content" @click="selected">
			<h2>{{ name }}</h2>
			<p>{{ description }}</p>
		</div>
		<div id="type">
			<font-awesome-icon
				v-if="type === 'GROUPS'"
				:icon="['fas', 'table-cells-large']"
				class="fa-2x"
			></font-awesome-icon>
			<font-awesome-icon
				v-else-if="type === 'KNOCKOUT'"
				:icon="['fas', 'user-group']"
				class="fa-2x"
			></font-awesome-icon>
		</div>
		<font-awesome-icon
			v-if="canEdit"
			id="settings"
			:icon="['fas', 'gear']"
			class="fa-2x"
			@click="settings"
		>
		</font-awesome-icon>
	</div>
</template>

<script lang="ts" setup>
const props = defineProps<{
	name: string
	description: string
	type: string
	canEdit: boolean
}>()

const emit = defineEmits(["selected", "settings"])

function selected() {
	emit("selected", props.name)
}

function settings() {
	console.log("settings")
	emit("settings", props.name)
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
	background-color: #d0d0d0;
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

#type {
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
	background-color: #c0c0c0;
}

@media only screen and (max-width: 900px) {
	#item {
		width: 100%;
		min-height: 100px;
		height: auto;
	}
}
</style>
