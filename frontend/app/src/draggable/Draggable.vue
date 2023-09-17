<template>
	<component ref="container" :is="props.tag">
		<slot v-for="(item, index) in list" :key="item['itemKey']" :item="item" :index="index" />
	</component>
</template>

<script setup lang="ts">
// @ts-ignore
import Sortable, { SortableEvent } from "sortablejs";
import { ref, ssrContextKey, watch } from "vue";

const props = withDefaults(defineProps<{
	list: any[], // TODO replace by generic component
	itemKey: string,
	tag: string,
	group: string,
	pull?: boolean | 'clone',
	put?: boolean | string[],
	animation?: number,
	disabled?: boolean,
	sort?: boolean,
	ghost?: string
}>(), {
	animation: 150,
	disabled: false,
	sort: true,
	pull: true,
	put: true,
	ghost: 'ghost'
})

const container = ref<HTMLElement | null>(null);
const sortable = ref<Sortable | null>()

watch(container, (el) => {
	if (!container)
		return

	sortable.value = Sortable.create(el, {
		group: { name: props.group, pull: props.pull, put: props.put },
		animation: props.animation,
		disabled: props.disabled,
		sort: props.sort,
		ghostClass: props.ghost,
		onEnd: (event: Sortable.SortableEvent) => {
			console.log(event.oldIndex)
			console.log(event.newIndex)
			console.log(event)
		},
	})
})
</script>

<style scoped>

</style>