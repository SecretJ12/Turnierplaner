<template>
	<component :is="tag" ref="container" v-bind="componentData" :key="index">
		<slot
			v-for="(item, i) in list"
			:key="item['itemKey']"
			:item="item"
			:index="i"
		/>
	</component>
</template>

<script setup lang="ts">
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import Sortable from "sortablejs"
import { ref, watch } from "vue"

const props = withDefaults(
	defineProps<{
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		list: any[]
		itemKey: string
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		tag: any
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		componentData?: any
		group: string
		pull?: boolean | "clone"
		put?: boolean | string[]
		animation?: number
		disabled?: boolean
		sort?: boolean
		ghost?: string
		single?: boolean
		wrap?: boolean
	}>(),
	{
		componentData: {},
		animation: 150,
		disabled: false,
		sort: true,
		pull: true,
		put: true,
		ghost: "ghost",
		single: false,
		wrap: false,
	},
)

if (props.single && props.list.length > 1) {
	console.log("single")
	console.log(props.list)
	throw new Error("List of single cannot contain more than one element")
}

const container = ref<HTMLElement | null>(null)
const sortable = ref<Sortable | null>()
watch(container, () => {
	create()
})

function create() {
	let el = container.value
	if (!el) return
	if (!(el instanceof HTMLElement))
		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-ignore
		el = <HTMLElement>el.$el

	if (sortable.value) {
		sortable.value?.destroy()
		sortable.value = null
	}

	sortable.value = Sortable.create(el, {
		group: { name: props.group, pull: props.pull, put: props.put },
		animation: props.animation,
		disabled: props.disabled,
		sort: props.sort,
		onChoose: (event: Sortable.SortableEvent) => {
			if (event.oldIndex === undefined) return
			selectedElement = props.list[event.oldIndex]
		},
		onRemove: (event: Sortable.SortableEvent) => {
			if (event.oldIndex === undefined) return

			if (targetSingle) {
				// eslint-disable-next-line @typescript-eslint/ban-ts-comment
				// @ts-ignore
				props.list.splice(event.oldIndex, 1, selectedElement)
			} else {
				props.list.splice(event.oldIndex, 1)
			}
			reload()
		},
		onAdd: (event: Sortable.SortableEvent) => {
			if (event.newIndex === undefined) return

			targetSingle = props.single && props.list.length === 1
			if (props.wrap) {
				props.list.splice(event.newIndex, 0, {
					id: (Math.random() * 900000000).toString(),
					playerA: [selectedElement],
					playerB: [],
				})
			} else if (props.single && props.list.length === 1) {
				// eslint-disable-next-line @typescript-eslint/ban-ts-comment
				// @ts-ignore
				selectedElement = props.list.splice(0, 1, selectedElement)[0]
			} else {
				// eslint-disable-next-line @typescript-eslint/ban-ts-comment
				// @ts-ignore
				props.list.splice(event.newIndex, 0, selectedElement)
			}
			reload()
		},
		onEnd: ({ from, to, oldIndex, newIndex }) => {
			if (from !== to) return
			if (oldIndex === undefined || newIndex === undefined) return

			props.list.splice(newIndex, 0, props.list.splice(oldIndex, 1)[0])
			reload()
		},
		revertOnSpill: true,
		removeOnSpill: false,
	})
	setGhost()
}

watch(
	() => [props.group, props.put, props.pull],
	() => {
		if (!sortable.value) return
		sortable.value.option("group", {
			name: props.group,
			pull: props.pull,
			put: props.put,
		})
	},
)
watch(
	() => [props.animation, props.animation, props.sort],
	() => {
		if (!sortable.value) return
		sortable.value?.option("animation", props.animation)
		sortable.value?.option("disabled", props.disabled)
		sortable.value?.option("sort", props.sort)
	},
)

watch(() => [props.ghost, props.single, props.list], setGhost)
function setGhost() {
	if (!sortable.value) return
	sortable.value?.option(
		"ghostClass",
		props.single && props.list.length === 1 ? "swapGhost" : props.ghost,
	)
}

// reload list to ensure a consistent state
const index = ref(0)

function reload() {
	index.value++
}
</script>
<script lang="ts">
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
let selectedElement = null
let targetSingle = false
</script>

<style scoped></style>
