using licar_wpf.Models;
using licar_wpf.ViewModels;
using licar_wpf.Views;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace licar_wpf.Services
{
    public class NavigationService
    {
        public event Action<ViewModelBase> CurrentViewModelChanged;
        
        public void NavigateTo(ViewModelBase viewModel)
        {
            CurrentViewModelChanged?.Invoke(viewModel);
        }
    }
}
