<template>
    <el-space style="margin-bottom: 2px;">
        <el-tag v-if="props.player.sex !== 'ANY'"
                effect="plain"
                size="small"
                round
        >
            {{ t("CompetitionSettings." + props.player.sex.toLowerCase()) }}
        </el-tag>

        <el-tooltip v-if="props.player.hasMinAge"
                    class="box-item"
                    effect="dark"
                    :content="t('ViewCompetition.born_before') + ' '
                    + props.player.minAge.toLocaleString(t('lang'), dateOptions)"
                    placement="top-start"
        >
            <el-tag
                effect="plain"
                size="small"
                round
            >
                {{ generateAboveTag() }}
            </el-tag>
        </el-tooltip>
        <el-tooltip v-if="props.player.hasMaxAge"
                    class="box-item"
                    effect="dark"
                    :content="t('ViewCompetition.born_after') + ' '
                    + props.player.maxAge.toLocaleString(t('lang'), dateOptions)"
                    placement="top-start"
        >
            <el-tag
                effect="plain"
                size="small"
                round
            >
                 {{ generateUnderTag() }}
            </el-tag>
        </el-tooltip>
    </el-space>
</template>

<script setup>
import {useI18n} from "vue-i18n";
const { t } = useI18n({inheritLocale: true})

const props = defineProps({
    beginGamePhase: Date,
    player: {
        type: Object,
        sex: String,
        hasMinAge: Boolean,
        minAge: Date,
        hasMaxAge: Boolean,
        maxAge: Date
}})

function generateAboveTag() {
    const dif = props.beginGamePhase.getFullYear() - props.player.minAge.getFullYear()
    return `Ü${dif}`
}
function generateUnderTag() {
    const dif = props.beginGamePhase.getFullYear() - props.player.maxAge.getFullYear()
    return `U${dif-1}`
}

const dateOptions = {
    year: "numeric",
    month: "numeric",
    day: "numeric"
}

const onlyYear = {
    year: "numeric"
}
</script>

<style scoped>

</style>