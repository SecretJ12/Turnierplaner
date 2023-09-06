<template>
	<!-- TODO optimize mobile -->
	<div class="sticky top-0">
		<header>
			<HeaderLeft />
			<HeaderRight />
		</header>
		<LoadingBar />
	</div>
</template>

<script lang="ts" setup>
import HeaderLeft from "./HeaderLeft.vue"
import HeaderRight from "./HeaderRight.vue"
import LoadingBar from "./LoadingBar.vue"
</script>

<style scoped>
header {
	height: 70px;
	display: flex;
	flex-direction: row;
	flex-wrap: nowrap;
	align-items: center;
	justify-content: space-between;
	position: sticky;
	left: 0;
	right: 0;
	top: 0;
	z-index: 10;
	background-color: white;
}
</style>
