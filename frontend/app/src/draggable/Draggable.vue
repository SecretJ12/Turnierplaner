<template>
	<component ref="container" :is="tag" v-bind="componentData" :key="index">
		<slot
			v-for="(item, index) in list"
			:key="item['itemKey']"
			:item="item"
			:index="index"
		/>
	</component>
</template>

<script setup lang="ts">
// @ts-ignore
import Sortable, { SortableEvent } from "sortablejs"
import { ref, watch } from "vue"

const props = withDefaults(
	defineProps<{
		list: any[]
		itemKey: string
		tag: any
		componentData?: any
		group: string
		pull?: boolean | "clone"
		put?: boolean | string[]
		animation?: number
		disabled?: boolean
		sort?: boolean
		ghost?: string
		swap?: boolean
	}>(),
	{
		animation: 150,
		disabled: false,
		sort: true,
		pull: true,
		put: true,
		ghost: "ghost",
		swap: false,
	},
)

const container = ref<HTMLElement | null>(null)
const sortable = ref<Sortable | null>()
watch(container, () => {
	create()
})

function create() {
	let el = container.value
	if (!el) return
	if (!(el instanceof HTMLElement))
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
		ghostClass: props.ghost,
		swap: props.swap,
		onChoose: (event: Sortable.SortableEvent) => {
			if (event.oldIndex === undefined) return
			selectedElement = props.list[event.oldIndex]
		},
		onRemove: (event: Sortable.SortableEvent) => {
			if (event.oldIndex === undefined) return
			props.list.splice(event.oldIndex, 1)
			reload()
		},
		onAdd: (event: Sortable.SortableEvent) => {
			if (event.newIndex === undefined) return
			// @ts-ignore
			props.list.splice(event.newIndex, 0, selectedElement)
			reload()
		},
		onEnd: ({ from, to, oldIndex, newIndex }) => {
			if (from !== to) return
			if (oldIndex === undefined || newIndex === undefined) return

			if (!props.swap) {
				props.list.splice(newIndex, 0, props.list.splice(oldIndex, 1)[0])
			} else {
				const a = props.list[oldIndex]
				const b = props.list[newIndex]
				props.list.splice(oldIndex, 1, b)
				props.list.splice(newIndex, 1, a)
			}
			reload()
		},
		revertOnSpill: true,
		removeOnSpill: false,
	})
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
	() => [props.animation, props.animation, props.sort, props.ghost, props.swap],
	() => {
		if (!sortable.value) return
		sortable.value?.option("animation", props.animation)
		sortable.value?.option("disabled", props.disabled)
		sortable.value?.option("sort", props.sort)
		sortable.value?.option("ghostClass", props.ghost)
		sortable.value?.option("swap", props.swap)
	},
)

// reload list to ensure a consistent state
const index = ref(0)

function reload() {
	index.value++
}
</script>
<script lang="ts">
// @ts-ignore
let selectedElement = null
</script>

<style scoped></style>