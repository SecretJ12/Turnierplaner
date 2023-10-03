<template>
	<div class="flex justify-content-center w-full">
		<div class="formgrid grid">
			<div class="field col-6">
				<label for="first_name">{{
					t("ViewPlayerRegistration.first_name.field")
				}}</label>
				<InputText
					id="first_name"
					type="text"
					v-bind="firstName"
					maxlength="30"
					class="w-full"
					:class="{ 'p-invalid': errors.firstName }"
				/>
				<InlineMessage v-if="errors.firstName" class="mt-2"
					>{{ t(errors.firstName || "") }}
				</InlineMessage>
			</div>
			<div class="field col-6">
				<label for="name">{{
					t("ViewPlayerRegistration.last_name.field")
				}}</label>
				<InputText
					id="name"
					type="text"
					v-bind="lastName"
					maxlength="30"
					class="w-full"
					:class="{ 'p-invalid': errors.lastName }"
				/>
				<InlineMessage v-if="errors.lastName" class="mt-2"
					>{{ t(errors.lastName || "") }}
				</InlineMessage>
			</div>
			<div class="field col-6">
				<label for="birthdate">{{
					t("ViewPlayerRegistration.birthdate.field")
				}}</label>
				<Calendar
					show-icon
					class="w-full"
					v-bind="birthdate"
					:manual-input="false"
					:date-format="t('date_format')"
					:class="{ 'p-invalid': errors.birthdate }"
				/>
				<InlineMessage v-if="errors.birthdate" class="mt-2"
					>{{ t(errors.birthdate || "") }}
				</InlineMessage>
			</div>
			<div class="field col-6">
				<label for="sex">{{ t("ViewPlayerRegistration.sex.field") }}</label>
				<Dropdown
					v-bind="sex"
					:options="[
						{ name: t('CompetitionSettings.male'), value: Sex.MALE },
						{ name: t('CompetitionSettings.female'), value: Sex.FEMALE },
					]"
					option-label="name"
					option-value="value"
					:placeholder="t(`CompetitionSettings.sex`)"
					class="w-full"
					:class="{ 'p-invalid': errors.sex }"
				>
					<template #option="slotProps">
						<div>
							{{ slotProps.option.name }}
						</div>
					</template>
				</Dropdown>
				<InlineMessage v-if="errors.sex" class="mt-2"
					>{{ t(errors.sex || "") }}
				</InlineMessage>
			</div>
			<div class="field col-12">
				<label for="email">{{ t("ViewPlayerRegistration.email.field") }}</label>
				<InputText
					id="email"
					type="text"
					v-bind="email"
					maxlength="30"
					class="w-full"
					:class="{ 'p-invalid': errors.email }"
				/>
				<InlineMessage v-if="errors.email" class="mt-2"
					>{{ t(errors.email || "") }}
				</InlineMessage>
			</div>
			<div class="field col-12">
				<label for="phone">{{ t("ViewPlayerRegistration.phone.field") }}</label>
				<InputText
					id="phone"
					type="text"
					v-bind="phone"
					maxlength="30"
					class="w-full"
					:class="{ 'p-invalid': errors.phone }"
				/>
				<InlineMessage v-if="errors.phone" class="mt-2"
					>{{ t(errors.phone || "") }}
				</InlineMessage>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import axios from "axios"
import { useI18n } from "vue-i18n"
import { Sex } from "@/interfaces/competition"
import { useForm } from "vee-validate"
import { toTypedSchema } from "@vee-validate/yup"
import { date, mixed, object, string } from "yup"
import { useToast } from "primevue/usetoast"
import { PlayerRegistration } from "@/interfaces/player"

const { t } = useI18n({ inheritLocale: true })

const phoneRegExp =
	/^(\+?\d{0,4})?\s?-?\s?(\(?\d{3}\)?)\s?-?\s?(\(?\d{3}\)?)\s?-?\s?(\(?\d{4}\)?)$/

const { defineInputBinds, errors, defineComponentBinds, handleSubmit } =
	useForm<PlayerRegistration>({
		validationSchema: toTypedSchema(
			object({
				firstName: string().min(4).max(40).required(),
				lastName: string().min(4).max(40).required(),
				sex: mixed().oneOf(Object.values(Sex)).required(),
				birthdate: date().required(),
				email: string().email(),
				phone: string().matches(
					phoneRegExp,
					t("ViewPlayerRegistration.phone.correct"),
				),
			}),
		),
		initialValues: {},
	})

const firstName = defineInputBinds("firstName")
const lastName = defineInputBinds("lastName")
const sex = defineComponentBinds("sex")
const birthdate = defineComponentBinds("birthdate")
const email = defineInputBinds("email")
const phone = defineInputBinds("phone")

const toast = useToast()

const emit = defineEmits(["registered"])

const onSubmit = handleSubmit((values) => {
	// TODO move to external files
	axios
		.post("/player/admin/registration", {
			firstName: values.firstName,
			lastName: values.lastName,
			sex: values.sex,
			birthdate: dateToJson(values.birthdate),
			email: values.email,
			phone: values.phone,
		})
		.then((player) => {
			toast.add({
				severity: "success",
				summary: t("ViewPlayerRegistration.registration_successful"),
				detail: t("ViewPlayerRegistration.after"),
				life: 3000,
			})
			emit("registered", player.data)
		})
		.catch((error) => {
			console.log(error)
			toast.add({
				severity: "error",
				summary: t("ViewPlayerRegistration.registration_failed"),
				detail: t("ViewPlayerRegistration.registration_failed_detail"),
				life: 3000,
			})
		})
})

function dateToJson(d: Date): string {
	return `${d.getFullYear()}-${d.getMonth() < 9 ? "0" : ""}${
		d.getMonth() + 1
	}-${d.getDate() < 10 ? "0" : ""}${d.getDate()}`
}

defineExpose({ onSubmit })
</script>

<style scoped></style>
