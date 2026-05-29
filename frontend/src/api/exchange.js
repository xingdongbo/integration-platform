import http from './http'

export function testExchange(interfaceCode, payload) {
  return http.post(`/exchange/${interfaceCode}/test`, payload)
}

export function retryExchange(traceId) {
  return http.post(`/retry-operations/${traceId}/retry`)
}
