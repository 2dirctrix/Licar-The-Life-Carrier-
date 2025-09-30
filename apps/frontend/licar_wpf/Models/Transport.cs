using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace licar_wpf.Models
{
    public class Transport
    {
        [JsonPropertyName("transportId")]
        public int TransportId { get; set; }

        [JsonPropertyName("requestedTime")]
        public DateTime RequestedTime { get; set; }

        [JsonPropertyName("sentTime")]
        public DateTime? SentTime { get; set; }

        [JsonPropertyName("arrivedTime")]
        public DateTime? ArrivedTime { get; set; }

        [JsonPropertyName("status")]
        public string Status { get; set; } // "requested", "in_transit", "completed"

        [JsonPropertyName("robotId")]
        public int RobotId { get; set; }

        [JsonPropertyName("nurseName")]
        public string NurseName { get; set; }

        [JsonPropertyName("pharmacistName")]
        public string PharmacistName { get; set; }

        [JsonPropertyName("patientId")]
        public int PatientId { get; set; }

        [JsonPropertyName("prescriptions")]
        public List<Prescription> Prescriptions { get; set; }
    }
}
