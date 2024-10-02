<template>
	<div class="horizontal-scroller">
		<div class="scroller-content">
			<slot></slot>
		</div>
	</div>
</template>

<script setup></script>

<style scoped>
.horizontal-scroller {
	width: 100%;
	overflow-x: auto;
	overflow-y: hidden;
}

.scroller-content {
	display: inline-block;
	white-space: nowrap;
}
</style>
