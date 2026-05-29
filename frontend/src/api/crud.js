import http from './http'

export function listPage(baseUrl, params) {
  return http.get(baseUrl, { params })
}

export function createRecord(baseUrl, payload) {
  return http.post(baseUrl, payload)
}

export function updateRecord(baseUrl, id, payload) {
  return http.put(`${baseUrl}/${id}`, payload)
}

export function deleteRecord(baseUrl, id) {
  return http.delete(`${baseUrl}/${id}`)
}
