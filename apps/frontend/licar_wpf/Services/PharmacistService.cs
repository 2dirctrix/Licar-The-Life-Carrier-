using licar_wpf.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace licar_wpf.Services
{
    public class PharmacistService
    {
        private readonly ApiClient _apiClient;

        public PharmacistService(ApiClient apiClient)
        {
            _apiClient = apiClient;
        }

        public async Task<List<Transport>> GetTransportsAsync()
        {
            return await _apiClient.GetAsync<List<Transport>>("/api/v1/transports");
        }

        public async Task<Transport> GetTransportDetailAsync(int transportId)
        {
            return await _apiClient.GetAsync<Transport>($"/api/v1/transports/{transportId}");
        }

        public async Task<List<Prescription>> GetValidPrescriptionAsync(int patientId, DateTime date)
        {
            string formattedDate = date.ToString("yyyy-MM-dd");

            return await _apiClient.GetAsync<List<Prescription>>($"/api/v1/prescriptions/valid?patient_id={patientId}&date={formattedDate}");
        }

        public async Task<Prescription> GetPrescriptionDetailAsync(int prescriptionId)
        {
            return await _apiClient.GetAsync<Prescription>($"/api/v1/prescriptions/{prescriptionId}");
        }

        public async Task<Prescription> GetPrescriptionDetailForTransportAsync (int transportId, int prescriptionId)
        {
            return await _apiClient.GetAsync<Prescription>($"/api/v1/transports/{transportId}/prescriptions/{prescriptionId}");
        }
    }
}
