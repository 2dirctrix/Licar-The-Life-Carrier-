using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace licar_wpf.Models
{
    public class Patient
    {
        [JsonPropertyName("patientId")]
        public int PatientId { get; set; }

        [JsonPropertyName("patientName")]
        public string Name { get; set; }

        [JsonPropertyName("roomNumber")]
        public int RoomNumber { get; set; }

        [JsonPropertyName("birthday")]
        public string? Birthday { get; set; }

        [JsonPropertyName("admissionDate")]
        public string? AdmissionDate { get; set; }

        [JsonPropertyName("dischargeDate")]
        public string? DischargeDate { get; set; }

        [JsonPropertyName("nurseName")]
        public string? NurseName { get; set; }
    }
}
