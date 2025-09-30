using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.Pkcs;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace licar_wpf.Models
{
    public class Notification
    {
        public string Message { get; set; }

        public DateTime ReceivedTime { get; set; }

        public int? TargetId { get; set; }

        public Notification(string message, int? targetId = null)
        {
            Message = message;
            ReceivedTime = DateTime.Now;
            TargetId = targetId;
        }
    }
}
