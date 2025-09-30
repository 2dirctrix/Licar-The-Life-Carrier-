using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace licar_wpf.Models
{
    public class Prescription
    {
        [JsonPropertyName("prescriptionId")]
        public int PrescriptionId { get; set; }

        [JsonPropertyName("patientName")]
        public string PatientName { get; set; }

        [JsonPropertyName("prescriptionDate")]
        public string Date { get; set; }

        [JsonPropertyName("period")]
        public int Period { get; set; }

        [JsonPropertyName("counsellingNote")]
        public string CounsellingNote { get; set; }

        [JsonPropertyName("drugs")]
        public List<Drug> Drugs { get; set; }

        [JsonIgnore]
        public string FormattedPeriodAndDateRange
        {
            get
            {
                if (DateTime.TryParse(Date, out DateTime startDate))
                {
                    DateTime endDate = startDate.AddDays(Period - 1);

                    return $"{Period} 일 ( {startDate:yyyy-MM-dd} ~ {endDate:yyyy-MM-dd} ) ";
                }

                return $"{Period} 일 ";
            }
        }
    }
}
