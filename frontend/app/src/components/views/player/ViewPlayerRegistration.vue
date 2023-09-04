<template>
	<div class="flex justify-content-center w-full">
		<div class="card" id="card">
			<h3>{{ t("ViewPlayerRegistration.headline") }}</h3>
			<div class="formgrid grid">
				<div class="field col-12">
					<label for="name">{{
						t("ViewPlayerRegistration.first_name.field")
					}}</label>
					<InputText
						id="name"
						type="text"
						v-bind="name"
						maxlength="30"
						class="w-full"
						:class="{ 'p-invalid': errors.name }"
					/>
					<InlineMessage v-if="errors.name" class="mt-2"
						>{{ t(errors.name || "") }}
					</InlineMessage>
				</div>
				<div class="field col-12">
					<label for="name">{{
						t("ViewPlayerRegistration.last_name.field")
					}}</label>
					<InputText
						id="name"
						type="text"
						v-bind="name"
						maxlength="30"
						class="w-full"
						:class="{ 'p-invalid': errors.name }"
					/>
					<InlineMessage v-if="errors.name" class="mt-2"
						>{{ t(errors.name || "") }}
					</InlineMessage>
				</div>
				<div class="field col-12">
					<Calendar showIcon />
				</div>
				<div class="field col-12">
					<Dropdown
						v-bind="playerASex"
						:options="[
							{ name: t('CompetitionSettings.male'), value: Sex.MALE },
							{ name: t('CompetitionSettings.female'), value: Sex.FEMALE },
							{ name: t('CompetitionSettings.any'), value: Sex.ANY },
						]"
						optionLabel="name"
						optionValue="value"
						:placeholder="t(`CompetitionSettings.sex`)"
						class="w-full md:w-14rem"
					>
						<template #option="slotProps">
							<div>
								{{ slotProps.option.name }}
							</div>
						</template>
					</Dropdown>
				</div>
			</div>
			<div class="field col-12">
				<label for="name">{{ t("ViewPlayerRegistration.email.field") }}</label>
				<InputText
					id="name"
					type="text"
					v-bind="name"
					maxlength="30"
					class="w-full"
					:class="{ 'p-invalid': errors.name }"
				/>
				<InlineMessage v-if="errors.name" class="mt-2"
					>{{ t(errors.name || "") }}
				</InlineMessage>
			</div>
			<div class="field col-12">
				<label for="name">{{ t("ViewPlayerRegistration.phone.field") }}</label>
				<InputText
					id="name"
					type="text"
					v-bind="name"
					maxlength="30"
					class="w-full"
					:class="{ 'p-invalid': errors.name }"
				/>
				<InlineMessage v-if="errors.name" class="mt-2"
					>{{ t(errors.name || "") }}
				</InlineMessage>
			</div>
			<div class="field col-12">
				<Button> </Button>
			</div>
		</div>
	</div>

	<div v-if="!registered" id="container">
		<div>
			<h2>
				{{ t("ViewPlayerRegistration.headline") }}
			</h2>
		</div>
		<div id="form">
			<el-form
				ref="formRef"
				:model="data"
				label-position="top"
				label-width="120px"
				scroll-to-error="scroll-to-error"
			>
				<el-row :gutter="20" class="row-bg" justify="space-between">
					<el-col :span="12">
						<el-form-item
							:label="t('ViewPlayerRegistration.first_name.field')"
							:rules="[
								{
									required: true,
									message: t('ViewPlayerRegistration.first_name.prompt'),
									trigger: 'blur',
								},
							]"
							prop="firstName"
						>
							<el-input v-model="data.firstName" />
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item
							:label="t('ViewPlayerRegistration.last_name.field')"
							:rules="[
								{
									required: true,
									message: t('ViewPlayerRegistration.last_name.prompt'),
									trigger: 'blur',
								},
							]"
							prop="lastName"
						>
							<el-input v-model="data.lastName" />
						</el-form-item>
					</el-col>
				</el-row>

				<el-row :gutter="20" class="row-bg" justify="space-between">
					<el-col :span="12">
						<el-form-item
							:label="t('ViewPlayerRegistration.sex.field')"
							:rules="[
								{
									required: true,
									message: t('ViewPlayerRegistration.sex.prompt'),
									trigger: 'blur',
								},
							]"
							prop="sex"
						>
							<el-select
								v-model="data.sex"
								:placeholder="t('ViewPlayerRegistration.sex.select')"
							>
								<el-option
									:label="t('ViewPlayerRegistration.sex.Option1')"
									value="MALE"
								/>
								<el-option
									:label="t('ViewPlayerRegistration.sex.Option2')"
									value="FEMALE"
								/>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item
							:label="t('ViewPlayerRegistration.birthdate.field')"
							required
						>
							<el-form-item
								:rules="[
									{
										type: 'date',
										required: true,
										message: t('ViewPlayerRegistration.birthdate.prompt'),
										trigger: 'blur',
									},
								]"
								prop="birthday"
							>
								<el-date-picker v-model="data.birthday" type="date" />
							</el-form-item>
						</el-form-item>
					</el-col>
				</el-row>

				<el-form-item
					:label="t('ViewPlayerRegistration.email.field')"
					:rules="[
						{
							required: true,
							message: t('ViewPlayerRegistration.email.empty'),
							trigger: 'blur',
						},
						{
							type: 'email',
							message: t('ViewPlayerRegistration.email.correct'),
							trigger: ['blur', 'change'],
						},
					]"
					prop="email"
				>
					<el-input v-model="data.email" />
				</el-form-item>

				<el-form-item
					:label="t('ViewPlayerRegistration.phone.field')"
					:rules="[
						{
							required: true,
							message: t('ViewPlayerRegistration.phone.empty'),
							trigger: 'blur',
						},
						{
							type: 'tel',
							message: t('ViewPlayerRegistration.phone.correct'),
							trigger: ['blur', 'change'],
						},
					]"
					prop="phone"
				>
					<el-input v-model="data.phone" />
				</el-form-item>

				<!-- TODO ich bin einverstanden, dass meine Daten gespeichert werden... (checkbox) -->

				<el-row class="row-bg" justify="end">
					<el-form-item>
						<el-button type="primary" @click="submitForm(formRef)"
							>Submit</el-button
						>
					</el-form-item>
				</el-row>
			</el-form>
		</div>
	</div>
	<div v-else id="container">
		<h2>
			{{ t("general.success") }}
		</h2>
		<p>
			{{ t("ViewPlayerRegistration.after") }}
		</p>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue"
import axios from "axios"
import { ElMessage } from "element-plus"
import { useI18n } from "vue-i18n"
import { Mode, Sex, SignUp, TourType } from "@/interfaces/competition"
import { useForm } from "vee-validate"
import { toTypedSchema } from "@vee-validate/yup"
import { boolean, date, mixed, object, string } from "yup"

const { t } = useI18n({ inheritLocale: true })

const formRef = ref<HTMLFormElement>()
const registered = ref(false)
const data = reactive<{
	firstName: string
	lastName: string
	sex: string
	birthday: null | Date
	email: string
	phone: string
}>({
	firstName: "",
	lastName: "",
	sex: "",
	birthday: null,
	email: "",
	phone: "",
})

const {
	values,
	defineInputBinds,
	errors,
	defineComponentBinds,
	handleSubmit,
	setFieldValue,
} = useForm({
	validationSchema: toTypedSchema(
		object({
			name: string()
				.min(4, t("validation.field_too_short"))
				.max(40, "validation.field_too_long")
				.required(t("validation.field_required")),
			description: string().max(50),
			tourType: mixed().oneOf(Object.values(TourType)).required(),
			mode: mixed().oneOf(Object.values(Mode)).required(),
			signUp: mixed().oneOf(Object.values(SignUp)).required(),

			playerA_Sex: mixed().oneOf(Object.values(Sex)).required(),
			playerA_hasMinAge: boolean(),
			playerA_minAge: date(),
			playerA_hasMaxAge: boolean(),
			playerA_maxAge: date(),

			playerB_different: boolean(),
			playerB_Sex: mixed().oneOf(Object.values(Sex)),
			playerB_hasMinAge: boolean(),
			playerB_minAge: date(),
			playerB_hasMaxAge: boolean(),
			playerB_maxAge: date(),
		}),
	),
	initialValues: {},
})

const name = defineInputBinds("name")
const description = defineInputBinds("description")
const selectedTourType = defineComponentBinds("tourType")
const selectedTourMode = defineComponentBinds("mode")
const signUp = defineComponentBinds("signUp")
const playerB_different = defineComponentBinds("playerB_different")
const playerASex = defineComponentBinds("playerA_Sex")
const playerBSex = defineComponentBinds("playerB_Sex")
const playerAHasMinAge = defineComponentBinds("playerA_hasMinAge")
const playerBHasMinAge = defineComponentBinds("playerB_hasMinAge")
const playerAHasMaxAge = defineComponentBinds("playerA_hasMaxAge")
const playerBHasMaxAge = defineComponentBinds("playerB_hasMaxAge")
const playerAMinAge = defineComponentBinds("playerA_minAge")
const playerAMaxAge = defineComponentBinds("playerA_maxAge")
const playerBMinAge = defineComponentBinds("playerB_minAge")
const playerBMaxAge = defineComponentBinds("playerB_maxAge")

function submitForm(formEl: HTMLFormElement | undefined) {
	if (!formEl) return
	formEl.validate((valid: boolean) => {
		if (valid) {
			if (data.birthday === null) return
			axios
				.post("/player/registration", {
					firstName: data.firstName,
					lastName: data.lastName,
					sex: data.sex,
					birthday: dateToJson(data.birthday),
					email: data.email,
					phone: data.phone,
				})
				.then(() => {
					ElMessage.success(t("ViewPlayerRegistration.registration_successful"))
					registered.value = true
				})
				.catch((error) => {
					console.log(error)
					ElMessage.error(t("ViewPlayerRegistration.registration_failed"))
				})
		} else {
			console.log("error submit!")
			return false
		}
	})
}

function dateToJson(d: Date): string {
	return `${d.getFullYear()}-${d.getMonth() < 9 ? "0" : ""}${
		d.getMonth() + 1
	}-${d.getDate() < 10 ? "0" : ""}${d.getDate()}`
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

#container {
	width: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
}
</style>
