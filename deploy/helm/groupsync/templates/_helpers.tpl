{{/*
Expand the name of the chart.
*/}}
{{- define "groupsync.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "groupsync.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "groupsync.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "groupsync.labels" -}}
helm.sh/groupsync: {{ include "groupsync.chart" . }}
{{ include "groupsync.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "groupsync.selectorLabels" -}}
app.kubernetes.io/name: {{ include "groupsync.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "groupsync.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "groupsync.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/* -------------------------------------------------------------------------------------------------------------- */}}
{{/* Docker Registry                                                                                                */}}
{{/* -------------------------------------------------------------------------------------------------------------- */}}

{{/*
Generate Docker registry secret name
*/}}
{{- define "app.registry-secret.name" -}}
{{- printf "registry-secret-%s" (include "groupsync.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Generate image pull secret
*/}}
{{- define "app.imagePullSecret" }}
{{- printf "{\"auths\": {\"%s\": {\"auth\": \"%s\"}}}" .Values.registryCredentials.registry (printf "%s:%s" .Values.registryCredentials.username .Values.registryCredentials.password | b64enc) | b64enc }}
{{- end }}

{{/*
Select image pull secret
*/}}
{{- define "app.imagePullSecretName" }}
    {{- if and ( .Values.registryCredentials.enabled) ( .Values.imagePullSecrets) }}
        {{- fail (printf "\nInvalid registryCredentials definition: do not pass both registryCredentials and imagePullSecret, select one or another.") }}
    {{- else if .Values.imagePullSecrets }}
        {{- print "imagePullSecrets:" }}
        {{- range .Values.imagePullSecrets }}
            {{- printf "\n  - name: %s" .name}}
        {{- end }}
    {{- else if and ( .Values.registryCredentials.enabled ) ((not .Values.imagePullSecrets)) }}
        {{- print "imagePullSecrets:" }}
        {{- printf "\n  - name: %s" (include "app.registry-secret.name" . ) }}
{{- end }}
{{- end }}
