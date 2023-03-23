<template>
  <div id="form">
    <el-form
        ref="formRef"
        :disabled="disabled"
        :model="data"
        label-width="120px"
        size="large"
        scroll-to-error="scroll-to-error"
        label-position="top"
    >
      <!-- Competition name -->
      <el-form-item
          :label="$t('general.name')"
          prop="name"
          :rules="[
                  {
                    required: true,
                    message: i18n.global.t('general.name_missing'),
                    trigger: 'blur',
                  }
              ]"
      >
        <el-input
            v-model="data.name"
            show-word-limit
            maxlength="30"
        />
      </el-form-item>

      <!-- Description -->
      <el-form-item
          :label="$t('general.description')"
          prop="description"
      >
        <el-input
            type="textarea"
            :autosize="{minRows: 3, maxRows: 5}"
            v-model="data.description"
            show-word-limit
            maxlength="100"
            autosize
        />
      </el-form-item>

      <!-- Type -->
      <el-form-item
          :label="$t('CompetitionSettings.type')"
          prop="description"
        >
        <el-select class="full-width"
          v-model="data.type"
        >
          <el-option
              :key="'KNOCKOUT'"
              :label='$t("CompetitionSettings.knockout")'
              :value="'KNOCKOUT'" />
          <el-option
              :key="'GROUPS'"
              :label='$t("CompetitionSettings.groups")'
              :value="'GROUPS'" />
        </el-select>
      </el-form-item>

      <el-row class="row-bg" justify="end">
        <el-form-item>
          <el-button type="primary" @click="submit(formRef)">
            {{ i18n.global.t("general.update") }}
          </el-button>
        </el-form-item>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import { i18n } from "@/main";
import { reactive, ref } from "vue";
import { router } from "@/main";
import axios from "axios";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";

const route = useRoute()

const formRef = ref()
const data = reactive({
  id: null,
  name: '',
  description: '',
  type: 'KNOCKOUT'
})

const disabled = ref(true)

axios.get(`/tournament/${route.params.tourId}/competition/${route.params.compId}/details`)
    .then((response) => {
      data.id = response.data.id
      data.name = response.data.name
      data.description = response.data.description
      data.type = response.data.type
      disabled.value = false
    })
    .catch((error) => {
      ElMessage.error(i18n.global.t("ViewEditCompetition.loadingDetailsFailed"))
      console.log(error)
      router.back();
    })

function submit(formRef) {
  if (!formRef)
    return
  formRef.validate((valid) => {
    if (valid) {
      const server_data = {
        id: data.id,
        name: data.name,
        description: data.description,
        type: data.type
      }

      axios.post(`/tournament/${route.params.tourId}/competition/update`, server_data)
        .then(_ => {
          router.push({path: "/tournament/" + route.params.tourId})
        })
        .catch(_ => {
          ElMessage.error(i18n.global.t("ViewCreateCompetition.creationFailed"))
        })
    } else {
      console.log('validation failed')
    }
  })
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

.full-width {
  width: 100%;
}
</style>