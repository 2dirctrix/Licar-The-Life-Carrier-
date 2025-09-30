using licar_wpf.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace licar_wpf.Services
{
    public class NurseService
    {
        private readonly ApiClient _apiClient;

        public NurseService(ApiClient client)
        {
            _apiClient = client;
        }

        public async Task<List<Patient>> GetPatientsAsync()
        {
            return await _apiClient.GetAsync<List<Patient>>($"/api/v1/patients");
        }

        public async Task<List<Patient>> SearchPatientsAsync(string name)
        {
            return await _apiClient.GetAsync<List<Patient>>($"/api/v1/patients?name={name}");
        }   

        public async Task<Patient> GetPatientDetailAsync(int patientId)
        {
            return await _apiClient.GetAsync<Patient>($"/api/v1/patients/{patientId}");
        }

        public async Task<Transport> RequestTransportAsync(int robotId, int nurseId, int pharmacistId, int patientId)
        {
            var requestBody = new
            {
                robotId,
                nurseId,
                pharmacistId,
                patientId
            };

            return await _apiClient.PostAsync<Transport>($"/api/v1/transports", requestBody);
        }

        public async Task<List<Prescription>> GetValidPrescriptionsAsync(int patientId, DateTime date)
        {
            string formattedDate = date.ToString("yyyy-MM-dd");

            return await _apiClient.GetAsync<List<Prescription>>($"/api/v1/prescriptions/valid?patient_id={patientId}&date={formattedDate}");
        }

        public async Task<Prescription> GetPrescriptionDetailsAsync(int prescriptionId)
        {
            return await _apiClient.GetAsync<Prescription>($"/api/v1/prescriptions/{prescriptionId}");
        }
    }
}
