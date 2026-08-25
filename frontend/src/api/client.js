import { tokenStorage } from './tokenStorage'
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api'

export class ApiError extends Error {
    constructor(message, status, data=null) {
        super(message)
        this.name = 'ApiError'
        this.status = status
        this.data = data
    }
}

async function request(path, options = {}) {
    const {
        method = 'GET',
        body,
        headers = {},
        ...rest
    } = options

    const requestHeaders = {
        Accept: 'application/json',
        ...headers
    }

    if(body != undefined)
        requestHeaders['Content-Type'] = 'application/json'

    const accessToken = tokenStorage.getAccessToken()

    if(accessToken)
        requestHeaders.Authorization = `Bearer ${accessToken}`

    const response = await fetch(`${API_BASE_URL}${path}`, {
        method,
        headers: requestHeaders,
        body: body !== undefined ? JSON.stringify(body) : undefined,
        ...rest,
    })

    const contentType = response.headers.get('content-type')
    const isJson = contentType?.includes('application/json')

    const data = response.status === 204
        ? null
        : isJson
            ? await response.json()
            : await response.text()
    
    if (!response.ok) {
        const message =
        data?.message ??
        data?.error ??
        `Request failed with status ${response.status}`

        throw new ApiError(message, response.status, data)
    }

    return data
}

export const apiClient = {
    get(path, options = {}) {
        return request(path, {
            ...options,
            method: 'GET',
        })
    },

    post(path, body, options = {}) {
        return request(path, {
            ...options,
            method: 'POST',
            body
        })
    },

    put(path, body, options = {}) {
        return request(path, {
            ...options,
            method: 'PUT',
            body
        })
    },

    delete(path, options = {}) {
        return request(path, {
            ...options,
            method: 'DELETE'
        })
    }
}