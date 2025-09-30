using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace licar_wpf.ViewModels
{
    public class ToastNotificationViewModel : ViewModelBase
    {
        public string Message { get; }
        public ICommand CloseCommand { get; }

        public ToastNotificationViewModel(string message, Action<ToastNotificationViewModel> closeAction)
        {
            Message = message;
            CloseCommand = new RelayCommand(_ => closeAction(this));
        }
    }
}
