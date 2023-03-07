<template>
  <div id="generalSettings">
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
  </div>
</template>

<script>
</script>

<style scoped>
#generalSettings {
  width: 100%;
  margin: 10px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  justify-content: center;
}
</style>