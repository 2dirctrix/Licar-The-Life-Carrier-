using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace licar_wpf.Models
{
    public class Pharmacist
    {
        [JsonPropertyName("pharmacistId")]
        public int PharmacistId { get; set; }

        [JsonPropertyName("name")]
        public string Name { get; set; }
    }
}
