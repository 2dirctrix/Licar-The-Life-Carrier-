using licar_wpf.ViewModels;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace licar_wpf.Services
{
    public class ToastNotificationService
    {
        public ObservableCollection<ToastNotificationViewModel> Notifications { get; } = new ObservableCollection<ToastNotificationViewModel>();

        public void Show(string message)
        {
            Application.Current.Dispatcher.Invoke(async () =>
            {
                var notificationViewModel = new ToastNotificationViewModel(message, Close);
                Notifications.Add(notificationViewModel);

                await Task.Delay(60 * 1000);
                Close(notificationViewModel);
            });
        }

        public void Close(ToastNotificationViewModel notification)
        {
            if (Notifications.Contains(notification))
            {
                Notifications.Remove(notification);
            }
        }
    }
}
