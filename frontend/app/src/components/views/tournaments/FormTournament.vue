<template>
	<div id="form">
		<el-form
			ref="formRef"
			:disabled="props.disabled"
			:model="props.data"
			label-position="top"
			label-width="120px"
			scroll-to-error="scroll-to-error"
			size="large"
		>
			<el-row :gutter="20" class="row-bg" justify="space-between">
				<el-col :span="20">
					<!-- Tournament name -->
					<el-form-item
						:label="t('general.name')"
						:rules="[
							{
								required: true,
								message: t('general.name_missing'),
								trigger: 'blur',
							},
						]"
						prop="name"
					>
						<el-input v-model="data.name" maxlength="30" show-word-limit />
					</el-form-item>
				</el-col>
				<el-col :span="4">
					<!-- Visible -->
					<el-form-item :label="t('TournamentSettings.visible')" prop="visible">
						<el-switch
							v-model="data.visible"
							style="
								--el-switch-on-color: #13ce66;
								--el-switch-off-color: #ff4949;
							"
						/>
					</el-form-item>
				</el-col>
			</el-row>

			<!-- Description -->
			<el-form-item :label="t('general.description')" prop="description">
				<el-input
					v-model="data.description"
					:autosize="{ minRows: 3, maxRows: 5 }"
					maxlength="100"
					show-word-limit
					type="textarea"
				/>
			</el-form-item>

			<!-- Times -->
			<el-divider />
			<!-- Begin -> End registration -->
			<el-form-item
				:label="t('TournamentSettings.registration_phase')"
				:rules="[
					{
						required: true,
						validator: checkDates,
						trigger: 'blur',
					},
				]"
				prop="registration_phase"
			>
				<el-date-picker
					v-model="data.registration_phase"
					end-placeholder="End date"
					start-placeholder="Start date"
					type="datetimerange"
				/>
			</el-form-item>
			<!-- Begin -> End game phase -->
			<el-form-item
				:label="t('TournamentSettings.game_phase')"
				:rules="[
					{
						required: true,
						validator: checkDates,
						trigger: 'blur',
					},
				]"
				prop="game_phase"
			>
				<el-date-picker
					v-model="data.game_phase"
					end-placeholder="End date"
					start-placeholder="Start date"
					type="datetimerange"
				/>
			</el-form-item>

			<el-row class="row-bg" justify="end">
				<el-form-item>
					<el-button type="primary" @click="submit(formRef)">
						{{ props.submitText }}
					</el-button>
				</el-form-item>
			</el-row>
		</el-form>
	</div>
</template>

<script lang="ts" setup>
import { ref } from "vue"
import {
	TournamentForm,
	tournamentFormClientToServer,
	TournamentServer,
} from "@/interfaces/tournament"
import { useI18n } from "vue-i18n"

const { t } = useI18n({ inheritLocale: true })

const formRef = ref<HTMLFormElement>()
const props = withDefaults(
	defineProps<{
		submitText: string
		disabled: boolean
		data: TournamentForm
	}>(),
	{
		disabled: false,
	},
)

const emit = defineEmits(["submit"])

function submit(formRef: HTMLFormElement | undefined) {
	if (!formRef) return
	formRef.validate((valid: boolean) => {
		if (valid) {
			const server_data: TournamentServer = tournamentFormClientToServer(
				props.data,
			)
			emit("submit", server_data)
		} else {
			console.log("validation failed")
		}
	})
}

const checkDates = (
	rule: any, // eslint-disable-line @typescript-eslint/no-explicit-any
	value: Date,
	callback: (arg0?: Error) => void,
) => {
	if (!value) {
		callback(new Error(t("TournamentSettings.missing_date")))
		return
	}
	if (
		props.data.registration_phase === null ||
		props.data.game_phase === null
	) {
		callback()
		return
	}
	if (
		new Date(props.data.registration_phase[1]) >
		new Date(props.data.game_phase[0])
	)
		callback(new Error(t("TournamentSettings.wrong_dates")))
	else callback()
}
</script>

<style scoped>
#form {
	width: 100%;
	margin: 10px;
	display: flex;
	flex-wrap: wrap;
	flex-direction: row;
	justify-content: center;
}
</style>
