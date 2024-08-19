import { ref, Ref, watch } from "vue"

export function sleep(milliseconds: number) {
	return new Promise((resolve) => setTimeout(resolve, milliseconds))
}

export function track<T, U>(
	func: (a: T) => U | null,
	def: U,
	data: T,
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	toWatch: Ref<any>[],
	isLoadings: Ref<boolean>[],
	isUpdating: Ref<boolean>,
	delay: number,
): { data: Ref<U>; reload: () => void } {
	const editableData = <Ref<U>>ref<U>(def)

	let buffer: U | null = null
	let running = false
	async function set(data: U) {
		buffer = data
		if (running) return

		running = true
		isUpdating.value = true
		while (buffer) {
			if (editableData.value) {
				editableData.value = def
				await sleep(delay)
			}
			editableData.value = buffer
			buffer = null
			await sleep(delay)
		}
		running = false
		isUpdating.value = false
	}

	watch(
		toWatch,
		() => {
			if (isLoadings.some((l) => l.value)) {
				set(def)
				return
			}

			const conv = func(data)
			if (editableData.value !== conv) set(conv ?? def)
		},
		{
			immediate: true,
		},
	)

	function reload() {
		set(func(data) ?? def)
	}

	return { data: editableData, reload }
}
