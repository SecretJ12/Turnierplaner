<template>
  <div id="form">
    <el-form
        ref="formRef"
        :model="data"
        label-position="top"
        label-width="120px"
        scroll-to-error="scroll-to-error"
        size="large"
    >
      <!-- Competition name -->
      <el-form-item
          :label="$t('general.name')"
          :rules="[
                  {
                    required: true,
                    message: i18n.global.t('general.name_missing'),
                    trigger: 'blur',
                  }
              ]"
          prop="name"
      >
        <el-input
            v-model="data.name"
            maxlength="30"
            show-word-limit
        />
      </el-form-item>

      <!-- Description -->
      <el-form-item
          :label="$t('general.description')"
          prop="description"
      >
        <el-input
            v-model="data.description"
            :autosize="{minRows: 3, maxRows: 5}"
            autosize
            maxlength="100"
            show-word-limit
            type="textarea"
        />
      </el-form-item>

      <!-- Type -->
      <el-form-item
          :label="$t('CompetitionSettings.type')"
          prop="description"
      >
        <el-select v-model="data.type"
                   class="full-width"
        >
          <el-option
              :key="'KNOCKOUT'"
              :label='$t("CompetitionSettings.knockout")'
              :value="'KNOCKOUT'"/>
          <el-option
              :key="'GROUPS'"
              :label='$t("CompetitionSettings.groups")'
              :value="'GROUPS'"/>
        </el-select>
      </el-form-item>

      <el-row class="row-bg" justify="end">
        <el-form-item>
          <el-button type="primary" @click="submit(formRef)">
            {{ i18n.global.t("general.create") }}
          </el-button>
        </el-form-item>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import {i18n, router} from "@/main";
import {reactive, ref} from "vue";
import axios from "axios";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";

const route = useRoute()

const formRef = ref()
const data = reactive({
  name: '',
  description: '',
  type: 'KNOCKOUT'
})

function submit(formRef) {
  if (!formRef)
    return
  formRef.validate((valid) => {
    if (valid) {
      const server_data = {
        name: data.name,
        description: data.description,
        type: data.type,
        tourName: route.params.tourId
      }

      axios.post(`/tournament/${route.params.tourId}/competition/add`, server_data)
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