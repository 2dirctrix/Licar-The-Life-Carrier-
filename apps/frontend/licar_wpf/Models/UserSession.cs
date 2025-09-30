using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Security.Cryptography.X509Certificates;

namespace licar_wpf.Models
{
    public enum UserRole
    {
        None,
        Nurse,
        Pharmacist
    }

    public class CurrentUser
    {
        public int Id {  get; set; }
        public string Name { get; set; }
        public UserRole Role { get; set; }
        public string Department { get; set; }
    }
    public static class UserSession
    {
        public static CurrentUser CurrentUser { get; private set; }

        public static void Login(CurrentUser user)
        {
            CurrentUser = user;
        }

        public static void Logout()
        {
            CurrentUser = null;
        }

        public static bool IsLoggedIn => CurrentUser != null;
    }
}
