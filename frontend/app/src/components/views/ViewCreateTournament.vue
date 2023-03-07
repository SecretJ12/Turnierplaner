<template>
  <div id="createTournament">
    <el-form
        ref="formRef"
        :model="data"
        label-width="120px"
        size="large"
        scroll-to-error="scroll-to-error"
        label-position="top"
    >
      <el-row :gutter="20" class="row-bg" justify="space-between">
        <el-col :span="20">
          <!-- Tournament name -->
          <el-form-item
              :label="'Name'"
              prop="name"
              :rules="[
                  {
                    required: true,
                    message: 'Prompt text',
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
        </el-col>
        <el-col :span="4">
          <!-- Visible -->
          <el-form-item
              :label="'Visible'"
              prop="visible"
          >
            <el-switch
                v-model="data.visible"
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- Description -->
      <el-form-item
          :label="'Description'"
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

      <!-- Times -->
      <el-divider />
      <!-- Begin -> End registration -->
      <el-form-item
          :label="'Registration phase'"
          prop="registration_phase"
          :rules="[
        {
          required: true,
          message: 'Prompt text',
          trigger: 'blur',
        }
    ]"
      >
        <el-date-picker
            v-model="data.registration_phase"
            type="datetimerange"
            start-placeholder="Start date"
            end-placeholder="End date"
        />
      </el-form-item>
      <!-- Begin -> End game phase -->
      <el-form-item
          :label="'Game phase'"
          prop="game_phase"
          :rules="[
        {
          required: true,
          message: 'Prompt text',
          trigger: 'blur',
        }
    ]"
      >
        <el-date-picker
            v-model="data.game_phase"
            type="datetimerange"
            start-placeholder="Start date"
            end-placeholder="End date"
        />
      </el-form-item>


      <el-row class="row-bg" justify="end">
        <el-col :span="5">
          <el-form-item>
            <el-button type="primary" @click="submit(formRef)">Create</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { settings } from '@/settings'
import axios from "axios";
import { ElMessage } from "element-plus";
import { router } from "@/main";

const formRef = ref()
const data = reactive({
  name: '',
  visible: true,
  description: '',
  registration_phase: null,
  game_phase: null
})

function submit(formRef) {
  if (!formRef)
    return
  formRef.validate((valid) => {
    if (valid) {
      console.log('submit!')
      axios.post(settings.BACKEND+"/tournament/add", data)
          .then(value => {
            router.push({path: "/tournament/" + data.name})
          })
          .catch(reason => {
            ElMessage.error("Couldn't create tournament")
          })
    } else {
      console.log('error submit!')
    }
  })
}
</script>

<style scoped>
#createTournament {
  width: 100%;
  margin: 10px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  justify-content: center;
}
</style>