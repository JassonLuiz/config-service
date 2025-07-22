package io.github.configservice.config_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ConfigEntryBatchDTO {

    private String key;
    private String description;
    private List<EnvironmentDTO> environments;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EnvironmentDTO> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<EnvironmentDTO> environments) {
        this.environments = environments;
    }

    public static class EnvironmentDTO {
        private String key;
        private String description;
        private List<ConfigDTO> configs;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<ConfigDTO> getConfigs() {
            return configs;
        }

        public void setConfigs(List<ConfigDTO> configs) {
            this.configs = configs;
        }
    }

    public static class ConfigDTO {
        private String key;
        private String value;
        private String description;
        private LocalDateTime updatedAt;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
