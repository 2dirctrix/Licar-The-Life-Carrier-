using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using System.Windows;

namespace licar_wpf.Services
{
    public class ApiClient
    {
        private readonly HttpClient _httpClient;

        private readonly JsonSerializerOptions _serializerOptions = new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        };

        public ApiClient(string baseUrl)
        {
            _httpClient = new HttpClient
            {
                BaseAddress = new Uri(baseUrl)
            };
            _httpClient.DefaultRequestHeaders.Add("Accept", "application/json");
        }

        public async Task<T> GetAsync<T>(string uri)
        {
            var response = await _httpClient.GetAsync(uri);
            response.EnsureSuccessStatusCode();

            return await response.Content.ReadFromJsonAsync<T>(_serializerOptions);
        }

        public async Task<TResponse> PostAsync<TResponse>(string uri, object data)
        {
            var response = await _httpClient.PostAsJsonAsync(uri, data, _serializerOptions);
            response.EnsureSuccessStatusCode();

            return await response.Content.ReadFromJsonAsync<TResponse>(_serializerOptions);
        }

        public async Task<TResponse> PatchASync<TResponse>(string uri, object data = null)
        {
            var content = data != null ? JsonContent.Create(data, options: _serializerOptions) : null;
            var request = new HttpRequestMessage(new HttpMethod("PATCH"), uri) { Content  = content };
            var response = await _httpClient.SendAsync(request);
            response.EnsureSuccessStatusCode();

            return await response.Content.ReadFromJsonAsync<TResponse>(_serializerOptions);
        }
    }
}
