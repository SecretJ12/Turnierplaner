<template>
    <!-- TODO sieht das hier gut aus? -->
    <el-row :gutter="20" class="row-bg" justify="space-between">
        <el-col :span="16">
            <span id="notice_register">{{ $t('ViewPlayerRegistration.not_found') }}</span>
        </el-col>
        <el-col :span="8">
            <el-button
                    style="width: 100%"
                    @click="playerRegistration"
            >
                {{ i18n.global.t('general.register') }}
            </el-button>
        </el-col>
    </el-row>
</template>

<script setup>
import {i18n, router} from "@/main";

function playerRegistration() {
    router.push({path: "/player/registration"})
}
</script>

<style scoped>
#notice_register {
    font-size: 14px;
    color: #404040;
}
</style>